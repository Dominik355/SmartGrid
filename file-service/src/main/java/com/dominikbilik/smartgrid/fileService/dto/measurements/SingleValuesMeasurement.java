package com.dominikbilik.smartgrid.fileService.dto.measurements;

import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementTypeByTime;
import com.dominikbilik.smartgrid.fileService.dto.records.ObisInfoRecord;
import com.dominikbilik.smartgrid.fileService.dto.records.SingleMeasurementRecord;

import java.util.ArrayList;
import java.util.List;

public class SingleValuesMeasurement<T extends SingleMeasurementRecord> extends Measurement {

    private List<T> records;
    private List<ObisInfoRecord> infoRecords;

    public SingleValuesMeasurement(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
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

    public List<ObisInfoRecord> getInfoRecords() {
        return infoRecords;
    }

    public void setInfoRecords(List<ObisInfoRecord> infoRecords) {
        this.infoRecords = infoRecords;
    }

    public void addInfoRecord(ObisInfoRecord record) {
        if (infoRecords == null) {
            infoRecords = new ArrayList<>();
        }
        infoRecords.add(record);
    }
}
