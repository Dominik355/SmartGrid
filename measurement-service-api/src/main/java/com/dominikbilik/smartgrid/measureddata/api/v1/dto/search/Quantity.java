package com.dominikbilik.smartgrid.measureddata.api.v1.dto.search;

public class Quantity {

    private String name;
    private String unit;

    private String medium;
    private String channel;
    private String measurementVariable;
    private String measurementType;
    private String tariff;
    private String previousMeasurement;

    public Quantity() {}

    public Quantity(String name) {
        this.name = name;
    }

    public Quantity(String name, String medium, String channel, String measurementVariable, String measurementType, String tariff, String previousMeasurement) {
        this.name = name;
        this.medium = medium;
        this.channel = channel;
        this.measurementVariable = measurementVariable;
        this.measurementType = measurementType;
        this.tariff = tariff;
        this.previousMeasurement = previousMeasurement;
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

    public boolean isObis() {
        return medium != null || channel != null || measurementType != null || measurementVariable != null || tariff != null;
    }

    @Override
    public String toString() {
        return "Quantity{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", medium='" + medium + '\'' +
                ", channel='" + channel + '\'' +
                ", measurementVariable='" + measurementVariable + '\'' +
                ", measurementType='" + measurementType + '\'' +
                ", tariff='" + tariff + '\'' +
                ", previousMeasurement='" + previousMeasurement + '\'' +
                '}';
    }
}
