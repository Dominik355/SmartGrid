package com.dominikbilik.smartgrid.device.api.rest;

import com.dominikbilik.smartgrid.device.api.v1.dto.RegisterDeviceRequest;
import com.dominikbilik.smartgrid.device.api.v1.dto.RegisterDeviceResponse;
import com.dominikbilik.smartgrid.device.service.DeviceService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/deviceService")
public class DeviceController {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @PostMapping(value = "registerDevice")
    public ResponseEntity<RegisterDeviceResponse> registerdevice(@Valid @RequestBody RegisterDeviceRequest request) {
        LOG.info("DeviceController.registerdevice called for : " + request);
        Assert.notNull(request, "request can not be null");
        Assert.isTrue(StringUtils.isNotBlank(request.getName()), "device name can not be blank");
        Assert.isTrue(StringUtils.isNotBlank(request.getIdInFilename()), "id can not be blank");
        Assert.isTrue(NumberUtils.isCreatable(request.getIdInFilename()) && Long.parseLong(request.getIdInFilename()) > 0, "ID has to consist only of positive whole numbers (long value)");

        return ResponseEntity.ok(new RegisterDeviceResponse(deviceService.registerDevice(request)));
    }

}
