package com.dominikbilik.smartgrid.measureddata.service.mappers;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.domain.entity.Measurement;

public class MeasurementMapper {

    public static Measurement dtoToEntity(MeasurementDto dto) {
        Measurement entity = new Measurement();
/*
        entity.setReferenceDeviceId();
        entity.setDeviceId();
        entity.setDataSet();
        entity.setDataSetId();
        entity.setSourceFileName();
        entity.setSourceFileId();
        entity.setMeasurementType();
        entity.setMeasurementTypeByDate();
        entity.setDateTimeFrom();
        entity.setDateTimeTo();
        entity.setRecordsType();
        entity.setInfoType();

        dto.getDeviceId();
        dto.getDeviceName();
        dto.getDeviceDataset();
        dto.getHeaders();
        dto.getSourceFileName();
        dto.getMeasurementType();
        dto.getMeasurementTypeByTime();
        dto.getFrom();
        dto.getTo();
*/
        return entity;
    }
}
