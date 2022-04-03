package com.dominikbilik.smartgrid.measureddata.api.v1.events;

import com.dominikbilik.smartgrid.common.model.Message;

public class ProcessMeasurementCommand implements Message {

    private Long measurementFileId;
    private Long deviceId;

    public ProcessMeasurementCommand() {}

    public ProcessMeasurementCommand(Long measurementFileId, Long deviceId) {
        this.measurementFileId = measurementFileId;
        this.deviceId = deviceId;
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

    @Override
    public String getTopic() {
        return "process_measurement";
    }
}
