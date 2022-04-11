package com.dominikbilik.smartgrid.fileService.api.v1.events;

import com.dominikbilik.smartgrid.common.model.Message;

public class ReverseProcessFileCommand implements Message {

    private Long fileId;

    public ReverseProcessFileCommand() {}

    public ReverseProcessFileCommand(Long fileId) {
        this.fileId = fileId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public String getTopic() {
        return "unprocess_file";
    }

    @Override
    public String toString() {
        return "ReverseProcessFileCommand{" +
                "fileId=" + fileId +
                '}';
    }
}
