package com.dominikbilik.smartgrid.device.api.v1.dto;

public class RegisterDeviceResponse {

    private Long deviceId;

    public RegisterDeviceResponse(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
}
