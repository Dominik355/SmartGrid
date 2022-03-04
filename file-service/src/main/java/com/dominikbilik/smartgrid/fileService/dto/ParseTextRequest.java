package com.dominikbilik.smartgrid.fileService.dto;

import com.dominikbilik.smartgrid.fileService.validation.annotations.ValidAblText;

import java.util.List;

public class ParseTextRequest {

    @ValidAblText
    private List<String> text;
    private String fileType;
    private String fileName;

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
