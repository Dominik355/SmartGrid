package com.dominikbilik.smartgrid.measureddata.domain.repository;


import com.dominikbilik.smartgrid.measureddata.domain.entity.Measurement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

}
