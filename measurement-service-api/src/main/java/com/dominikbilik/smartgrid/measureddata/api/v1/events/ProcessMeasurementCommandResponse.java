package com.dominikbilik.smartgrid.measureddata.api.v1.events;

import com.dominikbilik.smartgrid.common.model.MessageReply;

public class ProcessMeasurementCommandResponse implements MessageReply {

    private Long measurementId;

    public ProcessMeasurementCommandResponse() {}

    public ProcessMeasurementCommandResponse(Long measurementId) {
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
        return "process_measurement_reply";
    }

    @Override
    public String toString() {
        return "ProcessMeasurementCommandResponse{" +
                "measurementId=" + measurementId +
                '}';
    }
}
