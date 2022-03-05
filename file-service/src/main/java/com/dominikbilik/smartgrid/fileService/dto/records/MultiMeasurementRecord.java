package com.dominikbilik.smartgrid.fileService.dto.records;

public class MultiMeasurementRecord extends MeasurementRecord {

    static final long serialVersionUID = 444L;

    private Double[] values;

    private MultiMeasurementRecord () {}

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }

    public static class Builder<C extends MultiMeasurementRecord, T extends Builder<C, T>> extends MeasurementRecord.Builder<C, T> {

        public Builder() {
            this((C) new MultiMeasurementRecord());
        }

        private Builder(C obj) {
            super(obj);
        }

        public T withValues(Double[] values) {
            this.record.setValues(values);
            return (T) this;
        }

        public C build() {
            return this.record;
        }

    }
}
