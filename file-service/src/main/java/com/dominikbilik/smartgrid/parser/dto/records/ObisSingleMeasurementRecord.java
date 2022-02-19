package com.dominikbilik.smartgrid.parser.dto.records;

public class ObisSingleMeasurementRecord extends SingleMeasurementRecord {

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

    public static class Builder<C extends ObisSingleMeasurementRecord, T extends Builder<C,T>> extends SingleMeasurementRecord.Builder<C, T> {
        public Builder() {
            this((C) new ObisSingleMeasurementRecord());
        }

        private Builder(C obj) {
            super(obj);
        }

        public T withMedium(int medium) {
            this.record.setMedium(medium);
            return (T) this;
        }

        public T withChannel(int channel) {
            this.record.setChannel(channel);
            return (T) this;
        }

        public T withMeasurementVariable(int measurementVariable) {
            this.record.setMeasurementVariable(measurementVariable);
            return (T) this;
        }

        public T withMeasurementType(int measurementType) {
            this.record.setMeasurementType(measurementType);
            return (T) this;
        }

        public T withTariff(int tariff) {
            this.record.setTariff(tariff);
            return (T) this;
        }

        public T withPreviousMeasurement(int previousMeasurement) {
            this.record.setPreviousMeasurement(previousMeasurement);
            return (T) this;
        }

        public T withDescription(String description) {
            this.record.setDescription(description);
            return (T) this;
        }

        public C build() {
            return this.record;
        }

    }
}
