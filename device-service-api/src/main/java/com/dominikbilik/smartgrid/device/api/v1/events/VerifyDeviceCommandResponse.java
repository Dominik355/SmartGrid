package com.dominikbilik.smartgrid.device.api.v1.events;

import com.dominikbilik.smartgrid.common.model.MessageReply;

public class VerifyDeviceCommandResponse implements MessageReply {

    private Long deviceId;

    public VerifyDeviceCommandResponse() {}

    public VerifyDeviceCommandResponse(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String getTopic() {
        return "verify_device_reply";
    }
}
