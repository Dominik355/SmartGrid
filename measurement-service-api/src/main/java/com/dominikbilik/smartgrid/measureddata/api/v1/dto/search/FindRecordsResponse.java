package com.dominikbilik.smartgrid.measureddata.api.v1.dto.search;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FindRecordsResponse {

    private Long deviceId;
    private List<FoundRecords> foundRecords;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public List<FoundRecords> getFoundRecords() {
        return foundRecords;
    }

    public void setFoundRecords(List<FoundRecords> foundRecords) {
        this.foundRecords = foundRecords;
    }

    @Override
    public String toString() {
        return "FindRecordsResponse{" +
                "deviceId=" + deviceId +
                ", foundRecords=" + foundRecords +
                '}';
    }
}
