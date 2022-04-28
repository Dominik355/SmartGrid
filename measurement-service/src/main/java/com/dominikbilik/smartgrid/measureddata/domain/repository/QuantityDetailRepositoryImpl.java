package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import com.dominikbilik.smartgrid.measureddata.domain.entity.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

public class QuantityDetailRepositoryImpl implements QuantityDetailRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<QuantityDetail> ROW_MAPPER =
            (ResultSet rs, int rowNum) -> new QuantityDetail(
                    rs.getLong("id"),
                    rs.getString("quantity_name"),
                    rs.getString("unit"),
                    rs.getBoolean("is_obis"),
                    rs.getString("medium"),
                    rs.getString("channel"),
                    rs.getString("measurement_variable"),
                    rs.getString("measurement_type"),
                    rs.getString("tariff"),
                    rs.getString("previous_measurement")
            );

    @Override
    public QuantityDetail findByNameAndUnit(String name, String unit) {
        List<QuantityDetail> details = jdbcTemplate.query("SELECT * FROM quantity_detail WHERE quantity_name " + returnRightExpression(name) + " and unit " + returnRightExpression(unit), ROW_MAPPER);
        return (details != null && !details.isEmpty()) ? details.get(0) : null;
    }

    @Override
    public QuantityDetail findByObisCode(String unit,
                                               String medium,
                                               String channel,
                                               String measurementVariable,
                                               String measurementType,
                                               String tariff,
                                               String previousMeasurement) {
        List<QuantityDetail> details = jdbcTemplate.query(
                "select * from quantity_detail where " +
                        "is_obis = true and " +
                        "unit " + returnRightExpression(unit) + " and " +
                        "medium  " + returnRightExpression(medium) + " and " +
                        "channel " + returnRightExpression(channel) + "  and " +
                        "measurement_variable " + returnRightExpression(measurementVariable) + " and " +
                        "measurement_type " + returnRightExpression(measurementType) + " and " +
                        "tariff " + returnRightExpression(tariff) + " and " +
                        "previous_measurement " + returnRightExpression(previousMeasurement) + ";",
                ROW_MAPPER);
        return (details != null && !details.isEmpty()) ? details.get(0) : null;
    }

    @Override
    public List<QuantityDetail> findAllBySearchCriteria(String name,
                                                  String medium,
                                                  String channel,
                                                  String measurementVariable,
                                                  String measurementType,
                                                  String tariff,
                                                  String previousMeasurement) {
        return jdbcTemplate.query(
                "select * from quantity_detail where " +
                        "quantity_name " + returnRightExpression(name) + " and " +
                        "medium  " + returnRightExpression(medium) + " and " +
                        "channel " + returnRightExpression(channel) + "  and " +
                        "measurement_variable " + returnRightExpression(measurementVariable) + " and " +
                        "measurement_type " + returnRightExpression(measurementType) + " and " +
                        "tariff " + returnRightExpression(tariff) + " and " +
                        "previous_measurement " + returnRightExpression(previousMeasurement) + ";",
                ROW_MAPPER);
    }

    private String returnRightExpression(String value) {
        if (value != null) {
            return "= '" + value + "'";
        } else {
            return "is null";
        }
    }

}
