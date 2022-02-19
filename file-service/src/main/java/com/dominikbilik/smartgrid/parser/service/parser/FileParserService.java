package com.dominikbilik.smartgrid.parser.service.parser;

import com.dominikbilik.smartgrid.parser.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.parser.dto.records.ObisSingleMeasurementRecord;

import java.util.List;

public interface FileParserService {

    SingleValuesMeasurement<ObisSingleMeasurementRecord> parseSingleMeasurementFile(List<String> file, String fileName, String fileType);

    MultiValuesMeasurement<MultiMeasurementRecord> parseMultiMeasurementFile(List<String> file, String fileName, String fileType);

}
