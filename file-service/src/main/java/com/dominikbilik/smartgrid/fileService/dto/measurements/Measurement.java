package com.dominikbilik.smartgrid.fileService.dto.measurements;

import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementTypeByTime;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.dominikbilik.smartgrid.fileService.utils.ParserUtils.ABLUtils.DATE_FORMAT;
import static com.dominikbilik.smartgrid.fileService.utils.ParserUtils.ABLUtils.TIME_FORMAT;

public class Measurement implements Serializable {

    private String deviceId;
    private String deviceName;
    private String deviceDataset;

    private Map<String, String> headers;

    private String sourceFileName;

    private MeasurementType measurementType;//
    private MeasurementTypeByTime measurementTypeByTime;

    private LocalDateTime from;

    private LocalDateTime to;

    protected Measurement(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
        this.measurementType = measurementType;
        this.measurementTypeByTime = measurementTypeByTime;
    }

    protected Measurement() {
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

    public Optional<LocalDateTime> getDateTimeFromHeader() {
        if (getHeaders().containsKey("DATE") && getHeaders().containsKey("TIME")) {
            return Optional.of(LocalDate.parse(headers.get("DATE"), DATE_FORMAT).atTime(LocalTime.parse(headers.get("TIME"), TIME_FORMAT)));
        }
        return null;
    }
}