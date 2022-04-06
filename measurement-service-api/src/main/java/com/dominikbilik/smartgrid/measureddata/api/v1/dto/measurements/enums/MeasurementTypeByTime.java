package com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.enums;

public enum MeasurementTypeByTime {
    PERIODICAL, // date/time FROM and TO are different
    INSTANT; // date/time FROM and TO  are same
}
