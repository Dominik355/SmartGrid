package com.dominikbilik.smartgrid.measureddata.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(QuantityDetail.TABLE_NAME)
public class QuantityDetail implements SequenceID {

    public static final String TABLE_NAME = "quantity_detail";

    @Id
    private Long id;
    @Column("quantity_name")
    private String name;
    private String unit;
    private boolean isObis;

    private String medium;
    private String channel;
    private String measurementVariable;
    private String measurementType;
    private String tariff;
    private String previousMeasurement;


    public QuantityDetail() {};

    public QuantityDetail(String name, String unit, boolean isObisName) {
        this.name = name;
        this.unit = unit;
        this.isObis = isObisName;
    }

    public QuantityDetail(String unit, String measurementVariable, String measurementType) {
        this.unit = unit;
        this.measurementVariable = measurementVariable;
        this.measurementType = measurementType;
        this.isObis = true;
    }

    public QuantityDetail(String unit, boolean isObis, String medium, String channel, String measurementVariable, String measurementType, String tariff, String previousMeasurement) {
        this.unit = unit;
        this.isObis = isObis;
        this.medium = medium;
        this.channel = channel;
        this.measurementVariable = measurementVariable;
        this.measurementType = measurementType;
        this.tariff = tariff;
        this.previousMeasurement = previousMeasurement;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isObis() {
        return isObis;
    }

    public void setIsObis(boolean isObis) {
        isObis = isObis;
    }

    public void setObis(boolean obis) {
        isObis = obis;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMeasurementVariable() {
        return measurementVariable;
    }

    public void setMeasurementVariable(String measurementVariable) {
        this.measurementVariable = measurementVariable;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public String getPreviousMeasurement() {
        return previousMeasurement;
    }

    public void setPreviousMeasurement(String previousMeasurement) {
        this.previousMeasurement = previousMeasurement;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTablename() {
        return TABLE_NAME;
    }

    @Override
    public String toString() {
        return "QuantityDetail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", isObis=" + isObis +
                ", medium='" + medium + '\'' +
                ", channel='" + channel + '\'' +
                ", measurementVariable='" + measurementVariable + '\'' +
                ", measurementType='" + measurementType + '\'' +
                ", tariff='" + tariff + '\'' +
                ", previousMeasurement='" + previousMeasurement + '\'' +
                '}';
    }
}
