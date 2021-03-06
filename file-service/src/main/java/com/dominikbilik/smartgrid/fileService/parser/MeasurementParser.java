package com.dominikbilik.smartgrid.fileService.parser;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementTypeByTime;

import java.util.List;

/**
 * Stateful file parser.
 * Files represented as list of strings.
 * Create new instance every time.
 * Ideally use Supplier.
 * It is just not good ... rework into stateless with some object holding values, maybe some orchestration, or just parse line by line
 * Was created in a hurry with not enought information
 * @param <M> measurement type
 */
public abstract class MeasurementParser<M extends MeasurementDto> {

    protected final MeasurementType measurementType;
    protected final MeasurementTypeByTime measurementTypeByTime;

    protected M measurement;
    protected List<String> lines;
    protected String fileName;

    protected MeasurementParser(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
        this.measurementType = measurementType;
        this.measurementTypeByTime = measurementTypeByTime;
    }

    /**
     *
     * @param input - text readable file represented as ordered list of strings representing single lines
     * @return file parsed into measurement object
     */
    public abstract M parseToMeasurement(List<String> input, String fileName);

    protected abstract void validateInput();

}
