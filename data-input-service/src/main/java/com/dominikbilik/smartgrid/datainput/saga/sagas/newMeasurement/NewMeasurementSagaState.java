package com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement;

import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.datainput.saga.MessageSupplierBuilder;
import com.dominikbilik.smartgrid.datainput.saga.exception.InsufficientDataException;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommand;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommandResponse;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ReverseProcessFileCommand;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommand;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommandResponse;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class NewMeasurementSagaState {

    private static final Logger LOG = LoggerFactory.getLogger(NewMeasurementSagaState.class);

    private String sagaId;

    private String fileName;
    private String fileDeviceId;
    private String fileDeviceName;
    private Long fileId;
    private Long deviceId;
    private Long measurementId;
    private boolean deviceVerified;

    public NewMeasurementSagaState(String sagaId) {
        this.sagaId = sagaId;
    }

    public NewMeasurementSagaState(String sagaId, String fileName) {
        this.sagaId = sagaId;
        this.fileName = fileName;
    }

    public void handleProcessFileCommandResponse(ProcessFileCommandResponse response) {
        infoLog("Handling ProcessFileCommandResponse: {}", response);
        if (response == null || response.getFileId() == null ) {
            throw new InsufficientDataException("Response is missing FileId, so we assume that file was not created and we can not continue");
        }
        if (response.getDeviceNameFromFile() == null || response.getDeviceIdFromFile() == null) {
            throw new InsufficientDataException("Response is missing device name or id, so we can not assign file to device");
        }
        setFileDeviceId(response.getDeviceIdFromFile());
        setFileDeviceName(response.getDeviceNameFromFile());
        setFileName(response.getFileName());
        setFileId(response.getFileId());
        infoLog("NewMeasurementSagaState updated based on ProcessFileCommandResponse: " + this);
    }

    public void handleVerifyDeviceCommandResponse(VerifyDeviceCommandResponse response) {
        infoLog("Handling VerifyDeviceCommandResponse: {}", response);
        if (response == null || response.getDeviceId() == null) {
            throw new InsufficientDataException("Response is missing deviceId, so we assume that device is not registered and we can not continue");
        }
        setDeviceId(response.getDeviceId());
        setDeviceVerified(true);

        infoLog("NewMeasurementSagaState updated based on VerifyDeviceCommandResponse: " + this);
    }

    public void handleProcessMeasurementCommandResponse(ProcessMeasurementCommandResponse response) {
        infoLog("Handling ProcessMeasurementCommandResponse: {}", response);
        if (response == null || response.getMeasurementId() == null) {
            throw new InsufficientDataException("Response is missing measurementId, so we assume that measurement  was not created and we can not continue");
        }
        setMeasurementId(response.getMeasurementId());

        infoLog("NewMeasurementSagaState updated based on ProcessMeasurementCommandResponse: " + this);
    }

    public MessageSupplier<ProcessFileCommand> createProcessFileCommand() {
        infoLog("Creating MessageSupplier<ProcessFileCommand> for filename {} and sagaId as key {}", this.fileName, this.sagaId);
        return new MessageSupplierBuilder<ProcessFileCommand>()
                .withMessage(new ProcessFileCommand(this.fileName))
                .withKey(this.sagaId)
                .build();
    }

    public MessageSupplier<ReverseProcessFileCommand> createUnprocessFileCommand() {
        infoLog("Creating MessageSupplier<ReverseProcessFileCommand> for fileId {} and sagaId as key {}", this.fileId, this.sagaId);
        return new MessageSupplierBuilder<ReverseProcessFileCommand>()
                .withMessage(new ReverseProcessFileCommand(this.fileId))
                .withKey(this.sagaId)
                .build();
    }

    public MessageSupplier<VerifyDeviceCommand> createVerifyDeviceCommand() {
        infoLog("Creating MessageSupplier<VerifyDeviceCommand> for fileDeviceId {}, fileDeviceName {} and sagaId as key {}", this.fileDeviceId, this.fileDeviceName, this.sagaId);
        return new MessageSupplierBuilder<VerifyDeviceCommand>()
                .withMessage(new VerifyDeviceCommand(this.fileDeviceId, this.fileDeviceName))
                .withKey(this.sagaId)
                .build();
    }

    public MessageSupplier<ProcessMeasurementCommand> createProcessMeasurementCommand() {
        infoLog("Creating MessageSupplier<ProcessMeasurementCommand> for fileId {}, deviceId {} and sagaId as key {}", this.fileId, this.deviceId, this.sagaId);
        return new MessageSupplierBuilder<ProcessMeasurementCommand>()
                .withMessage(new ProcessMeasurementCommand(this.fileId, this.deviceId))
                .withKey(this.sagaId)
                .build();
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDeviceId() {
        return fileDeviceId;
    }

    public void setFileDeviceId(String fileDeviceId) {
        this.fileDeviceId = fileDeviceId;
    }

    public String getFileDeviceName() {
        return fileDeviceName;
    }

    public void setFileDeviceName(String fileDeviceName) {
        this.fileDeviceName = fileDeviceName;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isDeviceVerified() {
        return deviceVerified;
    }

    public void setDeviceVerified(boolean deviceVerified) {
        this.deviceVerified = deviceVerified;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private void infoLog(String format, Object... arguments) {
        LOG.info("[SagaId=" + this.sagaId + "]" + format, arguments);
    }
}
