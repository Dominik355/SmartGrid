package com.dominikbilik.smartgrid.measureddata.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(Measurement.TABLE_NAME)
public class Measurement implements SequenceID {

    public static final String TABLE_NAME = "measurement";

    @Id
    private Long id;
    private Long referenceDeviceId; // deviceId obtained right from device-service. So true reference
    private String deviceId; // deviceId recognizable by user -> might be same as referenceDeviceId
    private Long datasetId;
    private String sourceFilename;
    private Long sourceFileId; // refers to file-service
    private String measurementType; // *enum***
    private String measurementTypeByTime; // *enum***
    private LocalDateTime dateTimeFrom;
    private LocalDateTime dateTimeTo;
    private String recordsType; // *enum***
    private Integer recordsCount;
    private Integer frequencyInMinutes;

    public Measurement() {}

    public Measurement(String filename) {
        this.sourceFilename = filename;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Long getReferenceDeviceId() {
        return referenceDeviceId;
    }

    public void setReferenceDeviceId(Long referenceDeviceId) {
        this.referenceDeviceId = referenceDeviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public Long getSourceFileId() {
        return sourceFileId;
    }

    public void setSourceFileId(Long sourceFileId) {
        this.sourceFileId = sourceFileId;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public String getMeasurementTypeByTime() {
        return measurementTypeByTime;
    }

    public void setMeasurementTypeByTime(String measurementTypeByTime) {
        this.measurementTypeByTime = measurementTypeByTime;
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

    public String getRecordsType() {
        return recordsType;
    }

    public void setRecordsType(String recordsType) {
        this.recordsType = recordsType;
    }

    public Integer getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(Integer recordsCount) {
        this.recordsCount = recordsCount;
    }

    public Integer getFrequencyInMinutes() {
        return frequencyInMinutes;
    }

    public void setFrequencyInMinutes(Integer frequencyInMinutes) {
        this.frequencyInMinutes = frequencyInMinutes;
    }

    @Override
    public String getTablename() {
        return TABLE_NAME;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", referenceDeviceId=" + referenceDeviceId +
                ", deviceId='" + deviceId + '\'' +
                ", datasetId=" + datasetId +
                ", sourceFilename='" + sourceFilename + '\'' +
                ", sourceFileId=" + sourceFileId +
                ", measurementType='" + measurementType + '\'' +
                ", measurementTypeByTime='" + measurementTypeByTime + '\'' +
                ", dateTimeFrom=" + dateTimeFrom +
                ", dateTimeTo=" + dateTimeTo +
                ", recordsType='" + recordsType + '\'' +
                ", recordsCount=" + recordsCount +
                ", frequencyInMinutes=" + frequencyInMinutes +
                '}';
    }
}
