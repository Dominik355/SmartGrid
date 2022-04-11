package com.dominikbilik.smartgrid.measureddata.domain.entity;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * Record does not have direct relationship with measurement, so we can also process instantaneous values generated directly by the device
 * If we want to find out which measurement it belongs to, then we can search for records according to the time that the measurement includes
 */
public class Record {

    @Column("record_value")
    private Double value;
    private LocalDateTime dateTimeFrom; // if it is not a connection of multiple records, then the times are equal
    private LocalDateTime dateTimeTo;
    private Long datasetId;
    private Long quantityDetailsId;

    public Record(Double value, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Long datasetId, Long quantityDetailsId) {
        this.value = value;
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.datasetId = datasetId;
        this.quantityDetailsId = quantityDetailsId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public Long getQuantityDetailsId() {
        return quantityDetailsId;
    }

    public void setQuantityDetailsId(Long quantityDetailsId) {
        this.quantityDetailsId = quantityDetailsId;
    }

}
