package com.dominikbilik.smartgrid.datainput.api.proxy.endpoint;

import com.dominikbilik.smartgrid.datainput.service.NewMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/dataInput")
public class EnterController {

    private static final Logger LOG = LoggerFactory.getLogger(EnterController.class);

    @Autowired
    private NewMeasurementService newMeasurementService;

    @PostMapping(value = "parseMeasurementFile",  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> processMeasurementFile(
            @RequestParam(name = "file", required = true) MultipartFile file) {
        LOG.info("EnterEndpoint.processMeasurementFile(). processMeasurementFile = {}", getFileInfo(file));
        return ResponseEntity.ok(newMeasurementService.processNewMeasurement(file));
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
