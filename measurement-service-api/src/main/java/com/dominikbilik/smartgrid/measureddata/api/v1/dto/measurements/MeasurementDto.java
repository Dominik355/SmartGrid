package com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementTypeByTime;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class MeasurementDto implements Serializable {

    private String deviceId;
    private String deviceName;
    private String deviceDataset;

    private Map<String, String> headers;

    private String sourceFileName;
    private Long fileId;

    private MeasurementType measurementType;//
    private MeasurementTypeByTime measurementTypeByTime;

    private LocalDateTime from;

    private LocalDateTime to;

    protected MeasurementDto(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
        this.measurementType = measurementType;
        this.measurementTypeByTime = measurementTypeByTime;
    }

    public MeasurementDto() {
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDataset() {
        return deviceDataset;
    }

    public void setDeviceDataset(String deviceDataset) {
        this.deviceDataset = deviceDataset;
    }

    public Map<String, String> getHeaders() {
        return headers == null ? new HashMap<>() : headers;
    }

    public void addHeaders(Map<String, String> headers) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.putAll(headers);
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public MeasurementTypeByTime getMeasurementTypeByTime() {
        return measurementTypeByTime;
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

    // the patterns were in utils lcass but i moved to to api at 3 in the morning, this is hardcoded terrible piece of code just to make it work
    public Optional<LocalDateTime> getDateTimeFromHeader() {
        if (getHeaders().containsKey("DATE") && getHeaders().containsKey("TIME")) {
            try {
                return Optional.of(LocalDate.parse(headers.get("DATE"), DateTimeFormatter.ofPattern("dd.MM.yy")).atTime(LocalTime.parse(headers.get("TIME"), DateTimeFormatter.ofPattern("HH:mm:ss"))));
            } catch (Exception ex) {
                return Optional.of(LocalDate.parse(headers.get("DATE"), DateTimeFormatter.ofPattern("dd.MM.yy")).atTime(LocalTime.parse(headers.get("TIME"), DateTimeFormatter.ofPattern("HH:mm"))));
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceDataset='" + deviceDataset + '\'' +
                ", headers=" + headers +
                ", sourceFileName='" + sourceFileName + '\'' +
                ", fileId=" + fileId +
                ", measurementType=" + measurementType +
                ", measurementTypeByTime=" + measurementTypeByTime +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
