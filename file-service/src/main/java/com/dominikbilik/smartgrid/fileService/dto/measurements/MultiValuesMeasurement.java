package com.dominikbilik.smartgrid.fileService.dto.measurements;

import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementType;
import com.dominikbilik.smartgrid.fileService.dto.measurements.enums.MeasurementTypeByTime;
import com.dominikbilik.smartgrid.fileService.dto.records.MultiMeasurementRecord;

import java.util.ArrayList;
import java.util.List;

public class MultiValuesMeasurement<T extends MultiMeasurementRecord> extends Measurement {

    static final long serialVersionUID = 111L;

    private String[] quantityNames;
    private String[] quantityUnits;
    private List<T> records;
    private int frequencyInMinutes;
    private int measurementsCount;

    public MultiValuesMeasurement(MeasurementType measurementType, MeasurementTypeByTime measurementTypeByTime) {
        super(measurementType, measurementTypeByTime);
    }

    public String[] getQuantityNames() {
        return quantityNames;
    }

    public void setQuantityNames(String[] quantityNames) {
        this.quantityNames = quantityNames;
    }

    public String[] getQuantityUnits() {
        return quantityUnits;
    }

    public void setQuantityUnits(String[] quantityUnits) {
        this.quantityUnits = quantityUnits;
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

    public int getFrequencyInMinutes() {
        return frequencyInMinutes;
    }

    public void setFrequencyInMinutes(int frequencyInMinutes) {
        this.frequencyInMinutes = frequencyInMinutes;
    }

    public int getMeasurementsCount() {
        return measurementsCount;
    }

    public void setMeasurementsCount(int measurementsCount) {
        this.measurementsCount = measurementsCount;
    }
}
