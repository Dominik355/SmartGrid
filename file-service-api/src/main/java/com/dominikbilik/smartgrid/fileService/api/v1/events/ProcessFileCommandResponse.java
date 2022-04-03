package com.dominikbilik.smartgrid.fileService.api.v1.events;

import com.dominikbilik.smartgrid.common.model.MessageReply;

public class ProcessFileCommandResponse implements MessageReply {

    private String fileName;
    private Long fileId;
    private String deviceIdFromFile;
    private String deviceNameFromFile;

    public ProcessFileCommandResponse() {}

    public ProcessFileCommandResponse(String fileName, Long fileId, String deviceIdFromFile, String deviceNameFromFile) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.deviceIdFromFile = deviceIdFromFile;
        this.deviceNameFromFile = deviceNameFromFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getDeviceIdFromFile() {
        return deviceIdFromFile;
    }

    public void setDeviceIdFromFile(String deviceIdFromFile) {
        this.deviceIdFromFile = deviceIdFromFile;
    }

    public String getDeviceNameFromFile() {
        return deviceNameFromFile;
    }

    public void setDeviceNameFromFile(String deviceNameFromFile) {
        this.deviceNameFromFile = deviceNameFromFile;
    }

    @Override
    public String getTopic() {
        return "process_file";
    }

    @Override
    public String toString() {
        return "ProcessFileCommandResponse{" +
                "fileName='" + fileName + '\'' +
                ", fileId=" + fileId +
                ", deviceIdFromFile='" + deviceIdFromFile + '\'' +
                ", deviceNameFromFile='" + deviceNameFromFile + '\'' +
                '}';
    }
}
