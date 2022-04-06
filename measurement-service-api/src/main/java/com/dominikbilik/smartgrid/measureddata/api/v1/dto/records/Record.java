package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Record implements Serializable {

    private LocalDateTime dateTime;
    protected Record() {}

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public static class Builder<C extends Record, T extends Builder<C,T>> {
        C record;

        protected Builder(C record) {
            this.record = record;
        }

        public T withDateTime(LocalDateTime date) {
            this.record.setDateTime(date);
            return (T) this;
        }

    }

}
