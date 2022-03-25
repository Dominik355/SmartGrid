package com.dominikbilik.smartgrid.measureddata.api.dto.measurements.records;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class RecordDto implements Serializable {

    private LocalDateTime dateTime;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
