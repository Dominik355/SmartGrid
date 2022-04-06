package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records;

public abstract class MeasurementRecord extends Record {

    protected MeasurementRecord() {
        super();
    }

    public static class Builder<C extends MeasurementRecord, T extends Builder<C,T>> extends Record.Builder<C, T> {

        protected Builder(C record) {
            super(record);
        }

    }

}
