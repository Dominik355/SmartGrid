package com.dominikbilik.smartgrid.measureddata.api.v1.events;

import com.dominikbilik.smartgrid.common.model.Message;

public class ProcessMeasurementCommand implements Message {

    private Long measurementFileId;
    private Long deviceId;
    private String measurementType;

    public ProcessMeasurementCommand() {}

    public ProcessMeasurementCommand(Long measurementFileId, Long deviceId, String measurementType) {
        this.measurementFileId = measurementFileId;
        this.deviceId = deviceId;
        this.measurementType = measurementType;
    }

    public Long getMeasurementFileId() {
        return measurementFileId;
    }

    public void setMeasurementFileId(Long measurementFileId) {
        this.measurementFileId = measurementFileId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    @Override
    public String getTopic() {
        return "process_measurement";
    }

    @Override
    public String toString() {
        return "ProcessMeasurementCommand{" +
                "measurementFileId=" + measurementFileId +
                ", deviceId=" + deviceId +
                ", measurementType='" + measurementType + '\'' +
                '}';
    }
}
