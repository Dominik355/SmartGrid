package com.dominikbilik.smartgrid.datainput.saga.participants.deviceService;

import com.dominikbilik.smartgrid.datainput.saga.participants.Command;

public class VerifyDeviceCommand implements Command {

    private long deviceId;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
}
