package com.dominikbilik.smartgrid.measureddata.domain.converters;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.domain.entity.Measurement;

public class MeasurementConverter {

    public static Measurement dtoToEntity(MeasurementDto dto, Long deviceId) {
        Measurement entity = new Measurement();

        entity.setReferenceDeviceId(deviceId);
        entity.setDeviceId(dto.getDeviceId());
        //entity.setDataSetId(); // doplnit po vytvoreni alebo najdeni datasetu
        entity.setSourceFileId(dto.getFileId());
        entity.setSourceFilename(dto.getSourceFileName());
        entity.setMeasurementType(dto.getMeasurementType().name());
        entity.setMeasurementTypeByTime(dto.getMeasurementTypeByTime().name());
        entity.setDateTimeFrom(dto.getFrom());
        entity.setDateTimeTo(dto.getTo());

        if (dto instanceof MultiValuesMeasurement) {
            MultiValuesMeasurement mnt = (MultiValuesMeasurement) dto;
            entity.setRecordsCount(mnt.getMeasurementsCount());
            entity.setFrequencyInMinutes(mnt.getFrequencyInMinutes());
        } else {
            entity.setRecordsCount(((SingleValuesMeasurement) dto).getRecords().size());
        }

        return entity;
    }

}
