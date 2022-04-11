package com.dominikbilik.smartgrid.fileService.service.parser.impl;

import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.fileService.domain.dao.File;
import com.dominikbilik.smartgrid.fileService.domain.repository.FileRepository;
import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.fileService.parser.MeteoParser;
import com.dominikbilik.smartgrid.fileService.parser.ObisMultiMeasurementParser;
import com.dominikbilik.smartgrid.fileService.parser.ObisSingleMeasurementParser;
import com.dominikbilik.smartgrid.fileService.utils.FileUtils;
import com.dominikbilik.smartgrid.fileService.utils.common.SupplierFactory;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillMultiValuesMeasurement;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillSingleValuesMeasurement;
import static com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType.CLASSIC_OBIS;
import static com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType.METEO;

@Service
public class FileParserServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FileParserServiceImpl.class);

    @Autowired
    private FileRepository fileRepository;

    // because parsers are stateful, this force us to use new instance everytime (or declare Prototype bean and autowire that)
    private SupplierFactory<ObisSingleMeasurementParser> obisSingleParser = new SupplierFactory<>(() -> new ObisSingleMeasurementParser());
    private SupplierFactory<ObisMultiMeasurementParser> obisMultiParser = new SupplierFactory<>(() -> new ObisMultiMeasurementParser());
    private SupplierFactory<MeteoParser> meteoParser = new SupplierFactory<>(() -> new MeteoParser());

    public MeasurementDto parseFile(File file) {
        LOG.info("Parsing service method parseFile(File) called for file : {}", file);

        List<String> fileLines;
        try {
            fileLines = FileUtils.getLinesOfTextFile(file.getData());
        } catch (Exception e) {
            LOG.info("Exception occured while trying to get text out of File [{}]", file.getName());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        return parseFile(fileLines, file.getName(), file.getFileType());
    }

    public MeasurementDto parseFile(MultipartFile file, String fileName, String fileType) {
        LOG.info("Parsing service method parseFile(MultipartFile) called for : filename={}, filetype={}", (fileName != null ? fileName : file.getOriginalFilename()), fileType);
        if (fileName != null && !file.getOriginalFilename().equals(fileName)) {
            throw new IllegalArgumentException("Provided filename [{" + fileName + "}] does not match name of provided file [{" + file.getOriginalFilename() + "}]");
        } else if (StringUtils.isEmpty(fileName)) {
            LOG.info("Filename wasnt provided in request object, so we use filename from multipartfile");
            fileName = file.getOriginalFilename();
        }

        List<String> fileLines;
        try {
            fileLines = FileUtils.getLinesOfTextFile(file);
        } catch (IOException e) {
            LOG.info("Exception occured while trying to get text out of provided MultipartFile [{}]", file.getName());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        return parseFile(fileLines, fileName, fileType);
    }

    public MeasurementDto parseFile(List<String> fileLines, String fileName, String fileType) {
        LOG.info("Parsing service method parseFile(fileLines) called for : filename={}, filetype={}", fileName, fileType);
        Assert.isTrue(CollectionUtils.isNotEmpty(fileLines), "Lines can not be empty !!!");

        switch (MeasurementType.valueOf(fileType)) {
            case OBIS:
                return parseSingleMeasurementFile(fileLines, fileName, fileType);
            case CLASSIC_OBIS:
                return parseMultiMeasurementFile(fileLines, fileName, fileType);
            case METEO:
                return parseMultiMeasurementFile(fileLines, fileName, fileType);
            default:
                throw new IllegalStateException("Unexpected value for filetype: " + fileType);
        }
    }

    public MultiValuesMeasurement<MultiMeasurementRecord> parseMultiMeasurementFile(List<String> file, String fileName, String fileType) {
        Assert.noNullElements(file, "File list can not be null nor empty");
        Assert.notNull(fileName, "Filename can not be null");
        // parse based on fileType
        MultiValuesMeasurement<MultiMeasurementRecord> measurement = null;
        if (fileType.equalsIgnoreCase(CLASSIC_OBIS.toString())) {
            measurement = obisMultiParser.get().parseToMeasurement(file, fileName);
        } else if (fileType.equalsIgnoreCase(METEO.toString())) {
            measurement = meteoParser.get().parseToMeasurement(file, fileName);
        } else {
            throw new SmartGridParsingException("Not supported fileType for multi measurement file");
        }

        // use checker
        checkAndFillMultiValuesMeasurement(measurement);
        return measurement;
    }

    public SingleValuesMeasurement<ObisSingleMeasurementRecord> parseSingleMeasurementFile(List<String> file, String fileName, String fileType) {
        Assert.noNullElements(file, "File list can not be null nor empty");
        Assert.notNull(fileName, "Filename can not be null");
        // parse -> we have only 1 type
        SingleValuesMeasurement<ObisSingleMeasurementRecord> measurement = obisSingleParser.get().parseToMeasurement(file, fileName);

        // use checker
        checkAndFillSingleValuesMeasurement(measurement);
        return measurement;
    }

    // ****** TEMPORARY SUBSTITUTE HOLDER FOR IMPLEMENTATION OF STORING FILES
    // this part is here to substitute for file saving. Instead of saving file andgetting parsed object for request
    // we parse it first, time and store in map until someone calls for that
    // key -> "fileId" , value - parsed file into measurement
    private ConcurrentMap<Long, MeasurementDto> measurementMap = new ConcurrentHashMap<>();

    // just to make sure, we wont leak memory, cause some object cna be few MB big
    public void addMeasurementToMap(Long fileId, MeasurementDto measurementDto) {
        if (measurementMap.size() > 20) {
            measurementMap.clear();
        }
        measurementMap.put(fileId, measurementDto);
        LOG.info("measurementMap current state : " + measurementMap);
    }

    public MeasurementDto getMeasurementByFileId(long fileId) {
        LOG.info("returning measurement object for fileId {}", fileId);
        return measurementMap.remove(fileId);
    }

    @Transactional
    public ProcessFileCommandResponse parseSavedFile(String fileName) {
        LOG.info("Parsing saved file with name : " + fileName);
        File file = fileRepository.findByName(fileName);
        if (file == null) {
            throw new RuntimeException("File {} not found" + fileName);
        }

        MeasurementDto measurementDto = parseFile(file);
        addMeasurementToMap(file.getId(), measurementDto);

        return new ProcessFileCommandResponse(
                measurementDto.getSourceFileName(),
                file.getId(),
                measurementDto.getDeviceId(),
                measurementDto.getDeviceName(),
                measurementDto.getMeasurementType().name()
        );
    }

}
