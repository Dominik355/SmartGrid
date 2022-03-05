package com.dominikbilik.smartgrid.fileService.service.parser;

import com.dominikbilik.smartgrid.fileService.dto.measurements.Measurement;
import com.dominikbilik.smartgrid.fileService.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.fileService.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.fileService.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.fileService.dto.records.ObisSingleMeasurementRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileParserService {

    Measurement parseFile(MultipartFile file, String fileName, String fileType);

    SingleValuesMeasurement<ObisSingleMeasurementRecord> parseSingleMeasurementFile(List<String> file, String fileName, String fileType);

    MultiValuesMeasurement<MultiMeasurementRecord> parseMultiMeasurementFile(List<String> file, String fileName, String fileType);

}
