package com.dominikbilik.smartgrid.datainput.service;

import com.dominikbilik.smartgrid.datainput.saga.SagaManager;
import com.dominikbilik.smartgrid.datainput.saga.dao.SagaInstance;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.NewMeasurementHandler;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.NewMeasurementSaga;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class NewMeasurementService {

    private static final Logger LOG = LoggerFactory.getLogger(NewMeasurementService.class);

    private static final String UPLOAD_FILE_URL = "http://file-service/api/v1/files/uploadMeasurementFile";

    @Autowired
    private SagaManager sagaManager;

    @Autowired
    private NewMeasurementHandler handler;

    @Autowired
    private RestTemplate template;

    public SagaInstance processNewMeasurement(MultipartFile file) {
        String sagaSystemId = UUID.randomUUID().toString();
        String returnedFilename = null;

        try {
            returnedFilename = sendFileToFileService(file);
        } catch (Exception ex) {
            LOG.error("error occured while trying to send file into file-service.");
            ex.printStackTrace();
        }

        if (returnedFilename == null) {
            LOG.info("Sending file {} to file-service failed, we can not continue processing new file", file.getOriginalFilename());
            throw new RuntimeException("Error occured while processing file");
        }

        return sagaManager.start(new NewMeasurementSaga(handler, sagaSystemId,
                file.getOriginalFilename() != null ? file.getOriginalFilename() : file.getName()));
    }

    private String sendFileToFileService(MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        LOG.info("Sending HttpEntity to file-service saveFile endpoint: " + requestEntity);

        ResponseEntity<String> response = template.postForEntity(UPLOAD_FILE_URL, requestEntity, String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        return response.getBody();
    }

}
