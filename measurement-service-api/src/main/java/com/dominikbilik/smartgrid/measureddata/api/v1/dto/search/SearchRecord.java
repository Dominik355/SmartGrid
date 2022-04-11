package com.dominikbilik.smartgrid.measureddata.api.v1.dto.search;

import java.time.LocalDateTime;

public class SearchRecord {

    private LocalDateTime dateTime;
    private Double value;

    public SearchRecord(LocalDateTime dateTime, Double value) {
        this.dateTime = dateTime;
        this.value = value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SearchRecord{" +
                "dateTime=" + dateTime +
                ", value=" + value +
                '}';
    }
}
