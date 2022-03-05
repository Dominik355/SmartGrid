package com.dominikbilik.smartgrid.fileService.dto.records;

public class ObisSingleMeasurementRecord extends SingleMeasurementRecord {

    static final long serialVersionUID = 333L;

    private int medium;
    private int channel;
    private int measurementVariable;
    private int measurementType;
    private int tariff;
    private int previousMeasurement;
    private String description;

    private ObisSingleMeasurementRecord () {}

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

    public static class Builder extends SingleMeasurementRecord.Builder<ObisSingleMeasurementRecord, Builder> {
        public Builder() {
            this(new ObisSingleMeasurementRecord());
        }

        private Builder(ObisSingleMeasurementRecord obj) {
            super(obj);
        }

        public Builder withMedium(int medium) {
            this.record.setMedium(medium);
            return this;
        }

        public Builder withChannel(int channel) {
            this.record.setChannel(channel);
            return this;
        }

        public Builder withMeasurementVariable(int measurementVariable) {
            this.record.setMeasurementVariable(measurementVariable);
            return this;
        }

        public Builder withMeasurementType(int measurementType) {
            this.record.setMeasurementType(measurementType);
            return this;
        }

        public Builder withTariff(int tariff) {
            this.record.setTariff(tariff);
            return this;
        }

        public Builder withPreviousMeasurement(int previousMeasurement) {
            this.record.setPreviousMeasurement(previousMeasurement);
            return this;
        }

        public Builder withDescription(String description) {
            this.record.setDescription(description);
            return this;
        }

        public ObisSingleMeasurementRecord build() {
            return this.record;
        }

    }

    @Override
    public String toString() {
        return "ObisSingleMeasurementRecord{" +
                "medium=" + medium +
                ", channel=" + channel +
                ", measurementVariable=" + measurementVariable +
                ", measurementType=" + measurementType +
                ", tariff=" + tariff +
                ", unit=" + getUnit() +
                ", value=" + getValue() +
                ", dateTime=" + getDateTime() +
                ", previousMeasurement=" + previousMeasurement +
                ", description='" + description + '\'' +
                '}';
    }
}
