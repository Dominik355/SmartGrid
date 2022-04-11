package com.dominikbilik.smartgrid.measureddata.api.rest;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.search.FindRecordsRequest;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.search.FindRecordsResponse;
import com.dominikbilik.smartgrid.measureddata.service.RecordSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recordSearch")
public class RecordSearchController {

    private static final Logger LOG = LoggerFactory.getLogger(RecordSearchController.class);

    @Autowired
    private RecordSearchService recordSearchService;

    @GetMapping(value = "find")
    public ResponseEntity<Object> findRecords(@RequestBody FindRecordsRequest request) {
        LOG.info("RecordSearchController.findRecords called for request: {}", request);
        Assert.notNull(request.getDeviceId(), "deviceId can not be null !");
        Assert.notNull(request.getFrom(), "From can not be null !");
        Assert.notNull(request.getTo(), "To can not be null !");
        Assert.notNull(request.getQuantities() != null && !request.getQuantities().isEmpty(), "Quantities can not be empty !");

        FindRecordsResponse response = new FindRecordsResponse();
        response.setDeviceId(request.getDeviceId());

        try {
            response.setFoundRecords(recordSearchService.findRecords(request));
        } catch (Exception ex) {
            LOG.info("Exception occured while searching for records");
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(response);
    }

}
