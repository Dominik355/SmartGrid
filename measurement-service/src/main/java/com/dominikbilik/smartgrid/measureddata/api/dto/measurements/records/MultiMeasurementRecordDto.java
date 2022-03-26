package com.dominikbilik.smartgrid.measureddata.api.dto.measurements.records;

public class MultiMeasurementRecordDto extends MeasurementRecordDto {

    static final long serialVersionUID = 444L;

    private Double[] values;

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }

}
