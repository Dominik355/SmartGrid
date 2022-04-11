package com.dominikbilik.smartgrid.measureddata.api.v1.dto.search;

import java.time.LocalDateTime;
import java.util.List;

public class FindRecordsRequest {

    private Long deviceId;
    private LocalDateTime from;
    private LocalDateTime to;
    private List<Quantity> quantities;

    public FindRecordsRequest() {}

    public FindRecordsRequest(Long deviceId, LocalDateTime from, LocalDateTime to, List<Quantity> quantities) {
        this.deviceId = deviceId;
        this.from = from;
        this.to = to;
        this.quantities = quantities;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public List<Quantity> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Quantity> quantities) {
        this.quantities = quantities;
    }

    @Override
    public String toString() {
        return "FindRecordsRequest{" +
                "deviceId=" + deviceId +
                ", from=" + from +
                ", to=" + to +
                ", quantities=" + quantities +
                '}';
    }
}
