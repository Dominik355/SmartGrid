package com.dominikbilik.smartgrid.fileService.service.parser.impl;

import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.fileService.parser.MeteoParser;
import com.dominikbilik.smartgrid.fileService.parser.ObisMultiMeasurementParser;
import com.dominikbilik.smartgrid.fileService.parser.ObisSingleMeasurementParser;
import com.dominikbilik.smartgrid.fileService.service.parser.FileParserService;
import com.dominikbilik.smartgrid.fileService.utils.FileUtils;
import com.dominikbilik.smartgrid.fileService.utils.common.SupplierFactory;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.Measurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillMultiValuesMeasurement;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillSingleValuesMeasurement;
import static com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType.CLASSIC_OBIS;
import static com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType.METEO;

@Service
public class FileParserServiceImpl implements FileParserService {

    private static final Logger LOG = LoggerFactory.getLogger(FileParserServiceImpl.class);

    // because parsers are stateful, this force us to use new instance everytime (or declare Prototype bean and autowire that)
    private SupplierFactory<ObisSingleMeasurementParser> obisSingleParser = new SupplierFactory<>(() -> new ObisSingleMeasurementParser());
    private SupplierFactory<ObisMultiMeasurementParser> obisMultiParser = new SupplierFactory<>(() -> new ObisMultiMeasurementParser());
    private SupplierFactory<MeteoParser> meteoParser = new SupplierFactory<>(() -> new MeteoParser());

    @Override
    public Measurement parseFile(MultipartFile file, String fileName, String fileType) {
        LOG.info("Parsing service method parseFile() called for : filename={}, filetype={}", (fileName != null ? fileName : file.getOriginalFilename()), fileType);
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

    @Override
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

    @Override
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
    private ConcurrentMap<Long, Measurement> measurementMap = new ConcurrentHashMap<>();
    // key -> "fileName", value - multipartfile representing measurement
    private ConcurrentMap<String, MultipartFile> fileMap = new ConcurrentHashMap<>();

    // just to make sure, we wont leak memory, cause some object cna be few MB big
    public void addMeasurementToMap(Long fileId, Measurement measurement) {
        if (measurementMap.size() > 20) {
            measurementMap.clear();
        }
        measurementMap.put(fileId, measurement);
        System.out.println("measurementMap current state : " + measurementMap);
    }

    public Measurement getMeasurementByFileId(long fileId) {
        LOG.info("returning measurement object for fileId {}", fileId);
        return measurementMap.remove(fileId);
    }

    public void addFileToMap(String filename, MultipartFile file) {
        if (fileMap.size() > 20) {
            fileMap.clear();
        }
        fileMap.put(filename, file);
        LOG.info("fileMap current state : " + fileMap);
    }

    public MultipartFile getFileByFilename(String filename) {
        LOG.info("FileMap state : {}", fileMap);
        return fileMap.remove(filename);
    }

    // parse stored file in fileMap and put it into measurementMap to get these data later in saga
    // hard coded implementation just for demonstration !!!!!!!!!!!!
    public ProcessFileCommandResponse parseSavedFile(String fileName) {
        LOG.info("Parsing saved file with name : " + fileName);
        MultipartFile file = getFileByFilename(fileName);
        if (file == null) {
            throw new RuntimeException("File {} not found" + fileName);
        }
        LOG.info("File found : " + file);
        String currentFilename = file.getOriginalFilename() != null ?  file.getOriginalFilename() : file.getName();
        String fileType;
        if (currentFilename.contains("_meteo_")) {
            fileType = "METEO";
        } else if (currentFilename.contains("_P01_") || currentFilename.contains("_P02_")) {
            fileType = "CLASSIC_OBIS";
        } else {
            fileType = "OBIS";
        }

        Measurement measurement = parseFile(file, file.getOriginalFilename(), fileType);
        Long fileId = ThreadLocalRandom.current().nextLong(1000, 1000000);
        addMeasurementToMap(fileId, measurement);

        return new ProcessFileCommandResponse(
                measurement.getSourceFileName(),
                fileId,
                measurement.getDeviceId(),
                measurement.getDeviceName()
        );
    }

}
