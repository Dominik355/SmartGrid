package com.dominikbilik.smartgrid.fileService.api.rest.controller;

import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "uploadMeasurementFile",  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> uploadMeasurementFile(
            @RequestPart(name = "file") MultipartFile file) {
        Assert.notNull(file.getSize(), "File can not be empty");
        LOG.info("ParserController.uploadMeasurementFile() fileInfo = {}", getFileInfo(file));
        try {
            return ResponseEntity.ok(fileService.saveFile(file));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("exception", ex.getMessage())
                    .build();
        }
    }

    @DeleteMapping(value = "deleteMeasurementFile")
    public ResponseEntity<Boolean> deleteMeasurementFile(@RequestParam("fileId") Long fileId) {
        LOG.info("ParserController.deleteMeasurementFile(). fileId = {}", fileId);
        return ResponseEntity.ok(fileService.deleteFile(fileId));
    }

    private String getFileInfo(MultipartFile file) {
        StringBuilder builder = new StringBuilder();
        builder.append("multipartFile = [");
        builder.append("fileName: " + file.getOriginalFilename());
        builder.append(", fileSize: " + file.getSize());
        builder.append(", content-type: " + file.getContentType());
        builder.append("]");
        return builder.toString();
    }

}
