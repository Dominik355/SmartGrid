package com.dominikbilik.smartgrid.fileService.api.rest.controller;

import com.dominikbilik.smartgrid.fileService.api.rest.dto.ParseFileRequest;
import com.dominikbilik.smartgrid.fileService.service.parser.FileParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/fileParser")
public class ParserController {

    private static final Logger LOG = LoggerFactory.getLogger(ParserController.class);

    private final FileParserService fileParserService;

    public ParserController(FileParserService fileParserService) {
        this.fileParserService = fileParserService;
    }

    @PostMapping(value = "parseMeasurementFile",  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> parseMeasurementFile(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "parseFileRequest") ParseFileRequest request) {
        Assert.notNull(file.getSize(), "File can not be empty");
        LOG.info("ParserController.parseMeasurementFile [measurementType={}, fileName={}]", request.getMeasuremetType(), request.getFullFileName());
        return ResponseEntity.ok(fileParserService.parseFile(file, request.getFullFileName(), request.getMeasuremetType()));
    }

}
