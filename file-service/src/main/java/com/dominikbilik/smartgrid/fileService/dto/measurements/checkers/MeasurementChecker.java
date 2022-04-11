package com.dominikbilik.smartgrid.fileService.dto.measurements.checkers;

import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.SingleMeasurementRecord;
import org.springframework.util.Assert;

/**
 * Class is doing some final checks after parsing measurement to validate object
 */
public class MeasurementChecker {

    public static void checkAndFillMultiValuesMeasurement(MultiValuesMeasurement<MultiMeasurementRecord> measurement) {
        checkAndFillMeasurement(measurement);
        if (measurement.getFrom() == null) {
            // we take datetime from last record
            measurement.setFrom(measurement.getRecords().get(measurement.getRecords().size() - 1).getDateTime());
        }
    }

    public static void checkAndFillSingleValuesMeasurement(SingleValuesMeasurement measurement) {
        checkAndFillMeasurement(measurement);

        if (measurement.getFrom() == null) {
            // we take datetime from last record
            measurement.setFrom(measurement.getTo());
        }
    }

    public static void checkAndFillMeasurement(MeasurementDto measurementDto) {
        Assert.notNull(measurementDto.getSourceFileName(), "Source filename can not be empty");
        Assert.notNull(measurementDto.getMeasurementType(), "Measurement Type can not be empty");
        Assert.notNull(measurementDto.getMeasurementTypeByTime(), "Measurement TypeByTime can not be empty");

        if (measurementDto.getDeviceId() == null && measurementDto.getHeaders().get("ID") != null) {
            measurementDto.setDeviceId(measurementDto.getHeaders().get("ID"));
        }
        if (measurementDto.getDeviceDataset() == null && measurementDto.getHeaders().get("DATASET") != null) {
            measurementDto.setDeviceDataset(measurementDto.getHeaders().get("DATASET"));
            measurementDto.setDeviceName(measurementDto.getHeaders().get("DATASET"));
        }
        if (measurementDto.getTo() == null) {
            measurementDto.setTo(measurementDto.getDateTimeFromHeader()
                    .orElseThrow(() -> new SmartGridParsingException("Measurement headers does not contain DATE and TIME values")));
        }
    }

}
