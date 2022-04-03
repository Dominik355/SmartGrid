package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.records;

public class ObisSingleMeasurementRecordDto extends SingleMeasurementRecordDto {

    private int medium;
    private int channel;
    private int measurementVariable;
    private int measurementType;
    private int tariff;
    private int previousMeasurement;
    private String description;

    private ObisSingleMeasurementRecordDto() {}

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getMeasurementVariable() {
        return measurementVariable;
    }

    public void setMeasurementVariable(int measurementVariable) {
        this.measurementVariable = measurementVariable;
    }

    public int getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(int measurementType) {
        this.measurementType = measurementType;
    }

    public int getTariff() {
        return tariff;
    }

    public void setTariff(int tariff) {
        this.tariff = tariff;
    }

    public int getPreviousMeasurement() {
        return previousMeasurement;
    }

    public void setPreviousMeasurement(int previousMeasurement) {
        this.previousMeasurement = previousMeasurement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
