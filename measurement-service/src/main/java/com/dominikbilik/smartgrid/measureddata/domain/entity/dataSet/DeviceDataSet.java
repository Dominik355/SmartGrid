package com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet;

import com.dominikbilik.smartgrid.measureddata.domain.entity.SequenceID;
import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
@Table(DeviceDataSet.TABLE_NAME)
public class DeviceDataSet implements SequenceID {

    public static final String TABLE_NAME = "dataset";

    @Id
    private Long id;
    private int version;
    private Long referenceDeviceId; //refers to device service
    private String deviceId; // deviceId recognizable by user -> might be same as referenceDeviceId
    @Column("name")
    private String dataSetName;
    private String quantityTypeName; // enum -> OBIS, STRING
    private Set<QuantityRef> quantities; // list of column ID
    private int frequencyInSeconds; // in case we join same record, we need to fill this attribute ot be able to separate them after

    public DeviceDataSet() {}

    public DeviceDataSet(Long referenceDeviceId, String deviceId, String dataSetName, String quantityTypeName, Iterable<QuantityDetail> quantityDetails) {
        this.version = 0;
        this.referenceDeviceId = referenceDeviceId;
        this.deviceId = deviceId;
        this.dataSetName = dataSetName;
        this.quantityTypeName = quantityTypeName;
        this.addQuantityDetails(quantityDetails);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTablename() {
        return TABLE_NAME;
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getReferenceDeviceId() {
        return referenceDeviceId;
    }

    public void setReferenceDeviceId(Long referenceDeviceId) {
        this.referenceDeviceId = referenceDeviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getQuantityTypeName() {
        return quantityTypeName;
    }

    public void setQuantityTypeName(String quantityTypeName) {
        this.quantityTypeName = quantityTypeName;
    }

    public Set<QuantityRef> getQuantities() {
        return quantities;
    }

    public void setQuantities(Set<QuantityRef> quantities) {
        this.quantities = quantities;
    }

    public void addQuantityDetail(QuantityDetail quantityDetail) {
        if (quantities == null) {
            quantities = new HashSet<>();
        }
        quantities.add(new QuantityRef(this.getId(), quantityDetail.getId()));
    }

    public void addQuantityDetails(Iterable<QuantityDetail> quantityDetails) {
        if (quantities == null) {
            quantities = new HashSet<>();
        }
        quantities.addAll(StreamSupport.stream(quantityDetails.spliterator(), false)
                                        .map(detail -> new QuantityRef(this.getId(), detail.getId()))
                                        .collect(Collectors.toSet()));
    }

    public Set<Long> getQuantityDetailIds() {
        return quantities.stream()
                .map(QuantityRef::getQuantityDetailId)
                .collect(Collectors.toSet());
    }

    public int getFrequencyInSeconds() {
        return frequencyInSeconds;
    }

    public void setFrequencyInSeconds(int frequencyInSeconds) {
        this.frequencyInSeconds = frequencyInSeconds;
    }

    @Override
    public String toString() {
        return "DeviceDataSet{" +
                "id=" + id +
                ", version=" + version +
                ", referenceDeviceId=" + referenceDeviceId +
                ", deviceId='" + deviceId + '\'' +
                ", dataSetName='" + dataSetName + '\'' +
                ", quantityTypeName='" + quantityTypeName + '\'' +
                ", quantities=" + quantities +
                ", frequencyInSeconds=" + frequencyInSeconds +
                '}';
    }
}
