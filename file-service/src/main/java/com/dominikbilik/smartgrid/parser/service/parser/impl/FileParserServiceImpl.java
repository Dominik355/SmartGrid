package com.dominikbilik.smartgrid.parser.service.parser.impl;

import com.dominikbilik.smartgrid.parser.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.measurements.checkers.MeasurementChecker;
import com.dominikbilik.smartgrid.parser.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.parser.dto.records.ObisSingleMeasurementRecord;
import com.dominikbilik.smartgrid.parser.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.parser.parser.MeteoParser;
import com.dominikbilik.smartgrid.parser.parser.ObisMultiMeasurementParser;
import com.dominikbilik.smartgrid.parser.parser.ObisSingleMeasurementParser;
import com.dominikbilik.smartgrid.parser.service.parser.FileParserService;
import com.dominikbilik.smartgrid.parser.utils.common.SupplierFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class FileParserServiceImpl implements FileParserService {

    @Autowired
    private MeasurementChecker measurementChecker;

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
        if (fileType.equalsIgnoreCase("classicMultiObis")) {
            measurement = obisMultiParser.get().parseToMeasurement(file, fileName);
        } else if (fileType.equalsIgnoreCase("meteoMulti")) {
            measurement = meteoParser.get().parseToMeasurement(file, fileName);
        } else {
            throw new SmartGridParsingException("Not supported fileType for multi measurement file");
        }

        // use checker
        measurementChecker.checkAndFillMultiValuesMeasurement(measurement);
        
        return measurement;
    }

    @Override
    public SingleValuesMeasurement<ObisSingleMeasurementRecord> parseSingleMeasurementFile(List<String> file, String fileName, String fileType) {
        Assert.noNullElements(file, "File list can not be null nor empty");
        Assert.notNull(fileName, "Filename can not be null");
        // parse -> we have only 1 type
        SingleValuesMeasurement<ObisSingleMeasurementRecord> measurement = obisSingleParser.get().parseToMeasurement(file, fileName);

        // use checker
        measurementChecker.checkAndFillSingleValuesMeasurement(measurement);

        return measurement;
    }

}
