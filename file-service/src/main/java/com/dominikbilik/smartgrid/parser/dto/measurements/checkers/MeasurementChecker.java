package com.dominikbilik.smartgrid.parser.dto.measurements.checkers;

import com.dominikbilik.smartgrid.parser.dto.measurements.Measurement;
import com.dominikbilik.smartgrid.parser.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.parser.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.parser.dto.records.SingleMeasurementRecord;
import com.dominikbilik.smartgrid.parser.exception.SmartGridParsingException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Class is doing some final checks after parsing measurement to validate object
 */
@Component
public class MeasurementChecker {

    public void checkAndFillMultiValuesMeasurement(MultiValuesMeasurement<MultiMeasurementRecord> measurement) {
        checkAndFillMeasurement(measurement);
        if (measurement.getFrom() == null) {
            // we take datetime from last record
            measurement.setFrom(measurement.getRecords().get(measurement.getRecords().size() - 1).getDateTime());
        }
    }

    public void checkAndFillSingleValuesMeasurement(SingleValuesMeasurement<? extends SingleMeasurementRecord> measurement) {
        checkAndFillMeasurement(measurement);

        if (measurement.getFrom() == null) {
            // we take datetime from last record
            measurement.setFrom(measurement.getTo());
        }
    }

    public void checkAndFillMeasurement(Measurement measurement) {
        Assert.notNull(measurement.getSourceFileName(), "Source filename can not be empty");
        Assert.notNull(measurement.getMeasurementType(), "Measurement Type can not be empty");
        Assert.notNull(measurement.getMeasurementTypeByTime(), "Measurement TypeByTime can not be empty");

        if (measurement.getDeviceId() == null) {
            measurement.setDeviceId(measurement.getHeaders().get("ID"));
        }
        if (measurement.getDeviceName() == null) {
            measurement.setDeviceName(measurement.getHeaders().get("NAME"));
        }
        if (measurement.getTo() == null) {
            measurement.setTo(measurement.getDateTimeFromHeader()
                    .orElseThrow(() -> new SmartGridParsingException("Measurement headers does not contain DATE and TIME values")));
        }
    }

}
