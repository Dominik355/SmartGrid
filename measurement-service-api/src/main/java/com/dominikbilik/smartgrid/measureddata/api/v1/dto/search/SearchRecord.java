package com.dominikbilik.smartgrid.measureddata.api.v1.dto.search;

import java.time.LocalDateTime;

public class SearchRecord {

    private LocalDateTime dateTimeFrom;
    private LocalDateTime dateTimeTo;
    private Double value;

    public SearchRecord(LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Double value) {
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.value = value;
    }

    public LocalDateTime getDateTimeFrom() {
        return dateTimeFrom;
    }

    public void setDateTimeFrom(LocalDateTime dateTimeFrom) {
        this.dateTimeFrom = dateTimeFrom;
    }

    public LocalDateTime getDateTimeTo() {
        return dateTimeTo;
    }

    public void setDateTimeTo(LocalDateTime dateTimeTo) {
        this.dateTimeTo = dateTimeTo;
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
                "dateTimeFrom=" + dateTimeFrom +
                ", dateTimeTo=" + dateTimeTo +
                ", value=" + value +
                '}';
    }
}
