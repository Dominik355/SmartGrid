package com.dominikbilik.smartgrid.device.domain.repository;

import com.dominikbilik.smartgrid.device.domain.dao.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    boolean existsByIdInFilename(String idInFilename);

    Device findByIdInFilename(String idInFilename);

    List<Device> findAllByName(String name);
}
