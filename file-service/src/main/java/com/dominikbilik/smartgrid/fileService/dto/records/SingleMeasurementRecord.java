package com.dominikbilik.smartgrid.fileService.dto.records;

public class SingleMeasurementRecord extends MeasurementRecord {

    private String unit;
    private double value;

    protected SingleMeasurementRecord() {}

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

    public static class Builder<C extends SingleMeasurementRecord, T extends Builder<C,T>> extends MeasurementRecord.Builder<C, T> {

        protected Builder(C record) {
            super(record);
        }

        public T withUnit(String unit) {
            this.record.setUnit(unit);
            return (T) this;
        }

        public T withValue(double value) {
            this.record.setValue(value);
            return (T) this;
        }

        public C build() {
            return this.record;
        }
    }

}
