package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.records;

public class SingleMeasurementRecordDto extends MeasurementRecordDto {

    private String unit;
    private double value;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
