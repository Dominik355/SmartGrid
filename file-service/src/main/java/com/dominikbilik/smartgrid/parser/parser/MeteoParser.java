package com.dominikbilik.smartgrid.parser.parser;

import com.dominikbilik.smartgrid.parser.dto.measurements.MultiValuesMeasurement;

import java.util.List;

import static com.dominikbilik.smartgrid.parser.dto.measurements.enums.MeasurementType.CLASSIC;
import static com.dominikbilik.smartgrid.parser.dto.measurements.enums.MeasurementTypeByTime.PERIODICAL;

public class MeteoParser extends MeasurementParser<MultiValuesMeasurement> {

    public MeteoParser() {
        super(CLASSIC, PERIODICAL);
    }

    @Override
    public MultiValuesMeasurement parseToMeasurement(List<String> input, String fileName) {
        return null;
    }

    @Override
    protected void validateInput() {

    }
}
