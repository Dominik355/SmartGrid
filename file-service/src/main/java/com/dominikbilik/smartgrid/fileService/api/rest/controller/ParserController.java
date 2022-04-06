package com.dominikbilik.smartgrid.fileService.api.rest.controller;

import com.dominikbilik.smartgrid.fileService.api.rest.dto.ParseFileRequest;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileParserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/fileParser")
public class ParserController {

    private static final Logger LOG = LoggerFactory.getLogger(ParserController.class);

    private final FileParserServiceImpl fileParserService;

    public ParserController(FileParserServiceImpl fileParserService) {
        this.fileParserService = fileParserService;
    }

    @PostMapping(value = "parseMeasurementFile",  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> parseMeasurementFile(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "parseFileRequest") ParseFileRequest request) {
        Assert.notNull(file.getSize(), "File can not be empty");
        LOG.info("ParserController.parseMeasurementFile(). parseMeasurementFile = [measurementType={}, fileName={}], {}", request.getMeasuremetType(), request.getFullFileName(), getFileInfo(file));
        return ResponseEntity.ok(fileParserService.parseFile(file, request.getFullFileName(), request.getMeasuremetType()));
    }

    @GetMapping(value = "parseSavedFile")
    public ResponseEntity<Object> parseSavedFile(@RequestParam(name = "fileName") String fileName){
        Assert.notNull(fileName, "FileId can not be empty");
        LOG.info("ParserController.parseSavedFile(). fileName = {}}", fileName);
        return ResponseEntity.ok(fileParserService.parseSavedFile(fileName));
    }

    @GetMapping(value = "getParsedFile")
    public ResponseEntity<Object> getParsedMeasurement(@RequestParam(name = "fileId") Long fileId){
        Assert.notNull(fileId, "FileId can not be empty");
        LOG.info("ParserController.getParsedFile(). getParsedFile = [fileId={}]", fileId);
        return ResponseEntity.ok(fileParserService.getMeasurementByFileId(fileId));
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
