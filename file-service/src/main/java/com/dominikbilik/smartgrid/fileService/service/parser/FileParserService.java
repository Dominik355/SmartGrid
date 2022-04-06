package com.dominikbilik.smartgrid.fileService.service.parser;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.Measurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileParserService {

    Measurement parseFile(MultipartFile file, String fileName, String fileType);

    SingleValuesMeasurement<ObisSingleMeasurementRecord> parseSingleMeasurementFile(List<String> file, String fileName, String fileType);

    MultiValuesMeasurement<MultiMeasurementRecord> parseMultiMeasurementFile(List<String> file, String fileName, String fileType);

    Measurement getMeasurementByFileId(long fileId);

}
