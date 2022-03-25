package com.dominikbilik.smartgrid.fileService.service.parser.impl;

import com.dominikbilik.smartgrid.fileService.dto.measurements.Measurement;
import com.dominikbilik.smartgrid.fileService.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.fileService.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.fileService.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.fileService.dto.records.ObisSingleMeasurementRecord;
import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.fileService.parser.MeteoParser;
import com.dominikbilik.smartgrid.fileService.parser.ObisMultiMeasurementParser;
import com.dominikbilik.smartgrid.fileService.parser.ObisSingleMeasurementParser;
import com.dominikbilik.smartgrid.fileService.service.parser.FileParserService;
import com.dominikbilik.smartgrid.fileService.utils.FileUtils;
import com.dominikbilik.smartgrid.fileService.utils.common.SupplierFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillMultiValuesMeasurement;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillSingleValuesMeasurement;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType.*;

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

}
