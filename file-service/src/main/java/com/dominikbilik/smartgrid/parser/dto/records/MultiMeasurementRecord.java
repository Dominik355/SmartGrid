package com.dominikbilik.smartgrid.parser.dto.records;

import java.util.List;

public class MultiMeasurementRecord extends MeasurementRecord {

    private double[] values;

    private MultiMeasurementRecord () {}

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public static class Builder<C extends MultiMeasurementRecord, T extends Builder<C, T>> extends MeasurementRecord.Builder<C, T> {

        public Builder() {
            this((C) new MultiMeasurementRecord());
        }

        private Builder(C obj) {
            super(obj);
        }

        public T withValues(double[] values) {
            this.record.setValues(values);
            return (T) this;
        }

        public T withValues(List<Double> values) {
            this.record.setValues(values.stream().mapToDouble(d -> d).toArray());
            return (T) this;
        }

        public C build() {
            return this.record;
        }

    }
}
