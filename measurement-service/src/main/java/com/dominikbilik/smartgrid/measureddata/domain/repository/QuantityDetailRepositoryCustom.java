package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantityDetailRepositoryCustom {

    QuantityDetail findByNameAndUnit(String name, String unit);

    QuantityDetail findByObisCode(String unit,
                                        String medium,
                                        String channel,
                                        String measurementVariable,
                                        String measurementType,
                                        String tariff,
                                        String previousMeasurement);

    List<QuantityDetail> findAllBySearchCriteria(String name,
                                           String medium,
                                           String channel,
                                           String measurementVariable,
                                           String measurementType,
                                           String tariff,
                                           String previousMeasurement);
}
