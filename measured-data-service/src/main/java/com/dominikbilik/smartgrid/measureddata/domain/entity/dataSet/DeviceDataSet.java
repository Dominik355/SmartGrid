package com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet;

import java.util.List;

/**
    The DataSet corresponds to the description of the measured quantities within the measurement.
    The dataset relates to a specific device and is generically generated based on received measurements.
    If we are looking for measured values of a certain quantity for the device,
    we will use the dataset to find out whether the device measures the given quantity.

    Individual records refer to the dataset and not directly to the measurement.
    This will help us, for example, for quantities that use OBIS codes.
    For example, if we accept a request to find a certain quantity by its name for a device.
    Each device can have its own set of OBIS codes, so this quantity can be expressed by different obis codes.
    Using the dataset, we can find the correct code according to the device for which we are looking for a quantity.

    We could directly reference the quantity to the device, but instead we will refer to the sequence:"RECORD <-> DATASET <-> DEVICE".
    In this way we can store more information and simplify the acquisition of all measured quantities by the device.
    Instead of distinct select on quantityDetail directly on Record, we just get all the datasets for the given device and create a hashamp.
 */
public class DeviceDataSet {

    private Long id;
    private Long referenceDeviceId; //refers to device service
    private String deviceId; // deviceId recognizable by user -> might be same as referenceDeviceId
    private String dataSetName;
    private String quantityNameType; // enum -> OBIS, STRING
    private String implementedTable;
    private String implementedClass;
    private List<Long> quantities; // list of column ID

}
