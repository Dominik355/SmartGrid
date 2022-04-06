package com.dominikbilik.smartgrid.fileService.dto.measurements.checkers;

import com.dominikbilik.smartgrid.fileService.exception.SmartGridParsingException;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.Measurement;
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

    public static void checkAndFillSingleValuesMeasurement(SingleValuesMeasurement<? extends SingleMeasurementRecord> measurement) {
        checkAndFillMeasurement(measurement);

        if (measurement.getFrom() == null) {
            // we take datetime from last record
            measurement.setFrom(measurement.getTo());
        }
    }

    public static void checkAndFillMeasurement(Measurement measurement) {
        Assert.notNull(measurement.getSourceFileName(), "Source filename can not be empty");
        Assert.notNull(measurement.getMeasurementType(), "Measurement Type can not be empty");
        Assert.notNull(measurement.getMeasurementTypeByTime(), "Measurement TypeByTime can not be empty");

        if (measurement.getDeviceId() == null && measurement.getHeaders().get("ID") != null) {
            measurement.setDeviceId(measurement.getHeaders().get("ID"));
        }
        if (measurement.getDeviceDataset() == null && measurement.getHeaders().get("DATASET") != null) {
            measurement.setDeviceDataset(measurement.getHeaders().get("DATASET"));
        }
        if (measurement.getTo() == null) {
            measurement.setTo(measurement.getDateTimeFromHeader()
                    .orElseThrow(() -> new SmartGridParsingException("Measurement headers does not contain DATE and TIME values")));
        }
    }

}
