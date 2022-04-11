package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet.DeviceDataSet;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataSetRepository extends CrudRepository<DeviceDataSet, Long> {

    List<DeviceDataSet> findAllByReferenceDeviceId(Long referenceDeviceId);

    @Query("select d.* from dataset d join dataset_quantity_joins dq on d.id = dq.device_data_set where dq.quantity_detail = :id")
    List<DeviceDataSet> findByQuantityDetailId(@Param("id") Long id);

}
