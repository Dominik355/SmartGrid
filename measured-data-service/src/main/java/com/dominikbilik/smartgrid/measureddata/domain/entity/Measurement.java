package com.dominikbilik.smartgrid.measureddata.domain.entity;

import java.time.LocalDateTime;

public class Measurement {

    private Long id;
    private Long referenceDeviceId; //refers to device service
    private String deviceId; // deviceId recognizable by user -> might be same as referenceDeviceId
    private String dataSet; // if device is storing records in predefined object(table) it can define dataSet here, otherwise record values are stored separately
    private String dataSetId;
    private String sourceFileName;
    private Long sourceFileId; // refers to file-service
    private String measurementType; // *enum***
    private String measurementTypeByDate; // *enum***
    private LocalDateTime dateTimeFrom;
    private LocalDateTime dateTimeTo;
    private String recordsType; // *enum***
    private String infoType; // *enum*** Pure string, obis info
    private int recordsCount;
    private int frequencyInMinutes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
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

    public String getMeasurementTypeByDate() {
        return measurementTypeByDate;
    }

    public void setMeasurementTypeByDate(String measurementTypeByDate) {
        this.measurementTypeByDate = measurementTypeByDate;
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

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public int getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(int recordsCount) {
        this.recordsCount = recordsCount;
    }

    public int getFrequencyInMinutes() {
        return frequencyInMinutes;
    }

    public void setFrequencyInMinutes(int frequencyInMinutes) {
        this.frequencyInMinutes = frequencyInMinutes;
    }
}
