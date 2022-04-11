package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records;

public class ObisSingleMeasurementRecord extends SingleMeasurementRecord {

    static final long serialVersionUID = 333L;

    private Integer medium;
    private Integer channel;
    private Integer measurementVariable;
    private Integer measurementType;
    private Integer tariff;
    private Integer previousMeasurement;
    private String description;

    private ObisSingleMeasurementRecord () {}

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(Integer medium) {
        this.medium = medium;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getMeasurementVariable() {
        return measurementVariable;
    }

    public void setMeasurementVariable(Integer measurementVariable) {
        this.measurementVariable = measurementVariable;
    }

    public Integer getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(Integer measurementType) {
        this.measurementType = measurementType;
    }

    public Integer getTariff() {
        return tariff;
    }

    public void setTariff(Integer tariff) {
        this.tariff = tariff;
    }

    public Integer getPreviousMeasurement() {
        return previousMeasurement;
    }

    public void setPreviousMeasurement(Integer previousMeasurement) {
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

        public Builder withMedium(Integer medium) {
            this.record.setMedium(medium);
            return this;
        }

        public Builder withChannel(Integer channel) {
            this.record.setChannel(channel);
            return this;
        }

        public Builder withMeasurementVariable(Integer measurementVariable) {
            this.record.setMeasurementVariable(measurementVariable);
            return this;
        }

        public Builder withMeasurementType(Integer measurementType) {
            this.record.setMeasurementType(measurementType);
            return this;
        }

        public Builder withTariff(Integer tariff) {
            this.record.setTariff(tariff);
            return this;
        }

        public Builder withPreviousMeasurement(Integer previousMeasurement) {
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
