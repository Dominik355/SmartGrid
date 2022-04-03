package com.dominikbilik.smartgrid.measureddata.api.v1.events;

import com.dominikbilik.smartgrid.common.model.Message;

public class ReverseProcessMeasurementCommand implements Message {

    private Long measurementId;

    public ReverseProcessMeasurementCommand() {}

    public ReverseProcessMeasurementCommand(Long measurementId) {
        this.measurementId = measurementId;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    @Override
    public String getTopic() {
        return "unprocess_measurement";
    }
}
