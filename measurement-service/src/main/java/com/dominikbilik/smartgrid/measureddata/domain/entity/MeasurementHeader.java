package com.dominikbilik.smartgrid.measureddata.domain.entity;

import org.springframework.data.relational.core.mapping.Column;

public class MeasurementHeader {

    private long measurementId;
    @Column("header_key")
    private String key;
    @Column("header_value")
    private String value;

    public long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(long measurementId) {
        this.measurementId = measurementId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
