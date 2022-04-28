package com.dominikbilik.smartgrid.datainput.api.proxy.endpoint;

import com.dominikbilik.smartgrid.datainput.service.NewMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;

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


    //@Bean
    public CommandLineRunner commandLineRunner () {
        return args -> {
            File dir = new File("C:/Users/domin/IdeaProjects/SmartGrid/data-input-service/src/main/resources/startData");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    try {
                        System.out.println("Starting proccess for file : " + child.getName());
                        Instant startOverall = Instant.now();
                        MultipartFile multipartFile = new MockMultipartFile(child.getName(), child.getName(), "application/octet-stream", new FileInputStream(child));
                        System.out.println("Info : " + getFileInfo(multipartFile));
                        newMeasurementService.processNewMeasurement(multipartFile);
                        Instant endOverall = Instant.now();
                        System.out.println("Process ended for file : " + child.getName() + ", time: " + Duration.between(startOverall, endOverall));
                    } catch (Exception ex) {
                        System.out.println("exception for file: " + child.getName());
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println("Directory listing is null");
            }

        };
    }
}
