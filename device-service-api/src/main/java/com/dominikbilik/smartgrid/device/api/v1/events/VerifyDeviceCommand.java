package com.dominikbilik.smartgrid.device.api.v1.events;

import com.dominikbilik.smartgrid.common.model.Message;

public class VerifyDeviceCommand implements Message {

    private String fileDeviceId;
    private String deviceName;

    public VerifyDeviceCommand() {}

    public VerifyDeviceCommand(String fileDeviceId, String deviceName) {
        this.fileDeviceId = fileDeviceId;
        this.deviceName = deviceName;
    }

    public String getFileDeviceId() {
        return fileDeviceId;
    }

    public void setFileDeviceId(String fileDeviceId) {
        this.fileDeviceId = fileDeviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String getTopic() {
        return "verify_device";
    }
}
