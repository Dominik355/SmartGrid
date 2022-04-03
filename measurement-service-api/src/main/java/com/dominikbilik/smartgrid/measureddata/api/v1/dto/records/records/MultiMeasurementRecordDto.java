package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.records;

public class MultiMeasurementRecordDto extends MeasurementRecordDto {

    private Double[] values;

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }

}
