package com.dominikbilik.smartgrid.fileService.api.rest.dto;

import com.dominikbilik.smartgrid.fileService.api.validation.annotations.EnumValidator;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums.MeasurementType;

public class ParseFileRequest {

    private String fullFileName;

    @EnumValidator(
            enumClazz = MeasurementType.class,
            message = "String does not correspond to any type declared in MeasurementType Enum"
    )
    private String measuremetType;

    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public String getMeasuremetType() {
        return measuremetType;
    }

    public void setMeasuremetType(String measuremetType) {
        this.measuremetType = measuremetType;
    }

}
