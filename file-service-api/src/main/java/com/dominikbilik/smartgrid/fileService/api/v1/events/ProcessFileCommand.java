package com.dominikbilik.smartgrid.fileService.api.v1.events;

import com.dominikbilik.smartgrid.common.model.Message;

public class ProcessFileCommand implements Message {

    private String fileName;

    public ProcessFileCommand() {}

    public ProcessFileCommand(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getTopic() {
        return "process_file";
    }

    @Override
    public String toString() {
        return "ProcessFileCommand{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
