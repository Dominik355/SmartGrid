package com.dominikbilik.smartgrid.device.service;

import com.dominikbilik.smartgrid.device.api.v1.dto.RegisterDeviceRequest;
import com.dominikbilik.smartgrid.device.domain.dao.Device;
import com.dominikbilik.smartgrid.device.domain.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    public Long registerDevice(RegisterDeviceRequest request) {
        Long id = Long.parseLong(request.getIdInFilename());
        if (deviceRepository.existsById(id)) {
            throw new RuntimeException("Device with such ID already exists");
        }
        return deviceRepository.save(new Device(
                id,
                request.getName(),
                request.getIdInFilename(),
                LocalDateTime.now(),
                request.getOwner()
        )).getId();
    }

    public Long verifyDevice(String fileDeviceId, String deviceName) {
        return findDevice(fileDeviceId, deviceName).getId();
    }

    public Device findDevice(String fileDeviceId, String deviceName) {
        LOG.info("Going to find a device for fileDeviceId {} and deviceName {}", fileDeviceId, deviceName);
        if (fileDeviceId != null) {
            return deviceRepository.findByIdInFilename(fileDeviceId);
        }
        // we dont have ID, so try to find by name, if only 1 device has that name - return that
        LOG.info("We couldnt find device by its id in filename, so try to get by name if that is unique in table");
        List<Device> devices = deviceRepository.findAllByName(deviceName);
        if (devices.size() == 1) {
            return devices.get(0);
        }
        return null;
    }

}
