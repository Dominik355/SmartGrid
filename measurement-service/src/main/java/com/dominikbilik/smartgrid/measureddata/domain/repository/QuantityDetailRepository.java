package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantityDetailRepository extends CrudRepository<QuantityDetail, Long>, QuantityDetailRepositoryCustom {

    /*
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public QuantityDetail findByNameAndUnit(String name, String unit) {
        return jdbcTemplate.queryForObject("SELECT * FROM quantity_detail WHERE quantity_name = ? and unit = ?", QuantityDetail.class, name, unit);
    }

    public QuantityDetail findByObisCode(String unit,
                                         String medium,
                                         String channel,
                                         String measurementVariable,
                                         String measurementType,
                                         String tariff,
                                         String previousMeasurement) {
        return jdbcTemplate.queryForObject(
                "select d from quantity_detail d where " +
                "d.is_obis = true and " +
                "d.unit " + returnRightExpression(unit) + " and " +
                "d.medium  " + returnRightExpression(medium) + " and " +
                "d.channel " + returnRightExpression(channel) + "  and " +
                "d.measurement_variable " + returnRightExpression(measurementVariable) + " and " +
                "d.measurement_type " + returnRightExpression(measurementType) + " and " +
                "d.tariff " + returnRightExpression(tariff) + " and " +
                "d.previous_measurement " + returnRightExpression(previousMeasurement) + ";",
                        QuantityDetail.class);
    }
    /*
    @Query("select d from quantity_detail d where " +
            "d.is_obis = true and " +
            "(:unit is null or d.unit = :unit) and " +
            "(:medium is null or d.medium = :medium) and " +
            "(:channel is null or d.channel = :channel) and " +
            "(:channel is null or d.measurement_variable = :measurementVariable) and " +
            "(:measurement_type is null or d.measurement_type = :measurementType) and " +
            "(:tariff is null or d.tariff = :tariff) and " +
            "(:previous_measurement is null or d.previous_measurement = :previousMeasurement)")
    QuantityDetail findByObisCode(@Param("unit") String unit,
                                  @Param("medium") String medium,
                                  @Param("channel") String channel,
                                  @Param("measurementVariable") String measurementVariable,
                                  @Param("measurementType") String measurementType,
                                  @Param("tariff") String tariff,
                                  @Param("previousMeasurement") String previousMeasurement);

    public QuantityDetail findAllBySearchCriteria(String name,
                                         String medium,
                                         String channel,
                                         String measurementVariable,
                                         String measurementType,
                                         String tariff,
                                         String previousMeasurement) {
        return jdbcTemplate.queryForObject(
                "select d from quantity_detail d where " +
                        "d.name " + returnRightExpression(name) + " and " +
                        "d.medium  " + returnRightExpression(medium) + " and " +
                        "d.channel " + returnRightExpression(channel) + "  and " +
                        "d.measurement_variable " + returnRightExpression(measurementVariable) + " and " +
                        "d.measurement_type " + returnRightExpression(measurementType) + " and " +
                        "d.tariff " + returnRightExpression(tariff) + " and " +
                        "d.previous_measurement " + returnRightExpression(previousMeasurement) + ";",
                QuantityDetail.class);
    }

    @Query("select d from quantity_detail d where " +
            "(:quantity_name is null or d.quantity_name = :name) and " +
            "(:medium is null or d.medium = :medium) and " +
            "(:channel is null or d.channel = :channel) and " +
            "(:measurement_variable is null or d.measurement_variable = :measurementVariable) and " +
            "(:measurement_type is null or d.measurement_type = :measurementType) and " +
            "(:tariff is null or d.tariff = :tariff) and " +
            "(:previous_measurement is null or d.previous_measurement = :previousMeasurement)")
    QuantityDetail findAllBySearchCriteria(@Param("name") String name,
                                        @Param("medium") String medium,
                                        @Param("channel") String channel,
                                        @Param("measurementVariable") String measurementVariable,
                                        @Param("measurementType") String measurementType,
                                        @Param("tariff") String tariff,
                                        @Param("previousMeasurement") String previousMeasurement);

    private String returnRightExpression(String value) {
        if (value != null) {
            return "= " + value;
        } else {
            return "is null";
        }
    }
*/
}
