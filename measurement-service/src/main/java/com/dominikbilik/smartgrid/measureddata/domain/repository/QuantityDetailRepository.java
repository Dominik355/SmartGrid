package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantityDetailRepository extends CrudRepository<QuantityDetail, Long> {

    QuantityDetail findByNameAndUnit(String name, String unit);

    List<QuantityDetail> findAllByName(String name);

    @Query("select * from quantity_detail where " +
            "is_obis = true and " +
            "unit = :unit and " +
            "medium = :medium and " +
            "channel = :channel and " +
            "measurement_variable = :measurementVariable and " +
            "measurement_type = :measurementType and " +
            "tariff = :tariff and " +
            "previous_measurement = :previousMeasurement")
    QuantityDetail findByObisCode(@Param("unit") String unit,
                                  @Param("medium") String medium,
                                  @Param("channel") String channel,
                                  @Param("measurementVariable") String measurementVariable,
                                  @Param("measurementType") String measurementType,
                                  @Param("tariff") String tariff,
                                  @Param("previousMeasurement") String previousMeasurement);

    @Query("select * from quantity_detail where " +
            "quantity_name = :name and " +
            "medium = :medium and " +
            "channel = :channel and " +
            "measurement_variable = :measurementVariable and " +
            "measurement_type = :measurementType and " +
            "tariff = :tariff and " +
            "previous_measurement = :previousMeasurement")
    QuantityDetail findAllBySearchCriteria(@Param("name") String name,
                                        @Param("medium") String medium,
                                        @Param("channel") String channel,
                                        @Param("measurementVariable") String measurementVariable,
                                        @Param("measurementType") String measurementType,
                                        @Param("tariff") String tariff,
                                        @Param("previousMeasurement") String previousMeasurement);

}
