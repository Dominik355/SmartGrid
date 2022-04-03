package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.records;

import java.time.LocalDateTime;

public abstract class RecordDto {

    private LocalDateTime dateTime;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
