package com.dominikbilik.smartgrid.measureddata.api.dto.measurements;

import com.dominikbilik.smartgrid.measureddata.api.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.measureddata.api.dto.measurements.enums.MeasurementTypeByTime;
import com.dominikbilik.smartgrid.measureddata.api.dto.measurements.records.ObisInfoRecordDto;
import com.dominikbilik.smartgrid.measureddata.api.dto.measurements.records.SingleMeasurementRecordDto;

import java.util.ArrayList;
import java.util.List;

public class SingleValuesMeasurementDto<T extends SingleMeasurementRecordDto> extends MeasurementDto {

    static final long serialVersionUID = 222L;

    private List<T> records;
    private List<ObisInfoRecordDto> infoRecords;

    public SingleValuesMeasurementDto(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
        super(measurementType, measurementTypeByTime);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public void addRecord(T record) {
        if (this.records == null) {
            records = new ArrayList<>();
        }
        records.add(record);
    }

    public List<ObisInfoRecordDto> getInfoRecords() {
        return infoRecords;
    }

    public void setInfoRecords(List<ObisInfoRecordDto> infoRecords) {
        this.infoRecords = infoRecords;
    }

    public void addInfoRecord(ObisInfoRecordDto record) {
        if (infoRecords == null) {
            infoRecords = new ArrayList<>();
        }
        infoRecords.add(record);
    }
}
