package com.dominikbilik.smartgrid.measureddata.api.rest;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class FileServiceProxy {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceProxy.class);

    private static final String GET_MEASUREMENT_URL = "http://file-service/api/v1/fileParser/getParsedFile?fileId=";

    @Autowired
    private RestTemplate restTemplate;

    public Optional<MeasurementDto> getMeasurementFromParsedFile(Long fileId, String measurementType) {
        Assert.notNull(fileId, "FileId can not be null !!!");
        LOG.info("Obtaining parsed file into measurement from file-service with fileId={}, measurementType={}", fileId, measurementType);

        MeasurementDto body;
        if (measurementType.equalsIgnoreCase("OBIS")) {
            LOG.info("Obtaining measurement for type OBIS");
            SingleValuesMeasurement m = new SingleValuesMeasurement();
            ResponseEntity<SingleValuesMeasurement> response = restTemplate.getForEntity(GET_MEASUREMENT_URL + fileId, SingleValuesMeasurement.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            body = response.getBody();
        } else {
            LOG.info("Obtaining measurement for type CLASSIC_OBIS or METEO");
            ResponseEntity<MultiValuesMeasurement> response = restTemplate.getForEntity(GET_MEASUREMENT_URL + fileId, MultiValuesMeasurement.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            body = response.getBody();
        }
        LOG.info("Obtained file from file-service : " + body);
        return Optional.of(body);
    }

}
