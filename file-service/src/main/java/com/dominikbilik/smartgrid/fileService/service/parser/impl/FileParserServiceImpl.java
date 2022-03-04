package com.dominikbilik.smartgrid.fileService.service.parser.impl;

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
import com.dominikbilik.smartgrid.fileService.utils.common.SupplierFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillMultiValuesMeasurement;
import static com.dominikbilik.smartgrid.fileService.dto.measurements.checkers.MeasurementChecker.checkAndFillSingleValuesMeasurement;

@Service
public class FileParserServiceImpl implements FileParserService {

    // because parsers are stateful, this force us to use new instance everytime (or declare Prototype bean and autowire that)
    private SupplierFactory<ObisSingleMeasurementParser> obisSingleParser = new SupplierFactory<>(() -> new ObisSingleMeasurementParser());
    private SupplierFactory<ObisMultiMeasurementParser> obisMultiParser = new SupplierFactory<>(() -> new ObisMultiMeasurementParser());
    private SupplierFactory<MeteoParser> meteoParser = new SupplierFactory<>(() -> new MeteoParser());

    @Override
    public MultiValuesMeasurement<MultiMeasurementRecord> parseMultiMeasurementFile(List<String> file, String fileName, String fileType) {
        Assert.noNullElements(file, "File list can not be null nor empty");
        Assert.notNull(fileName, "Filename can not be null");
        // parse based on fileType
        MultiValuesMeasurement<MultiMeasurementRecord> measurement = null;
        if (fileType.equalsIgnoreCase(MeasurementType.CLASSIC_OBIS.toString())) {
            measurement = obisMultiParser.get().parseToMeasurement(file, fileName);
        } else if (fileType.equalsIgnoreCase(MeasurementType.METEO.toString())) {
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
