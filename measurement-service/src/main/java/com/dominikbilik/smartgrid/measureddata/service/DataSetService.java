package com.dominikbilik.smartgrid.measureddata.service;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet.DeviceDataSet;
import com.dominikbilik.smartgrid.measureddata.domain.repository.DataSetRepository;
import com.dominikbilik.smartgrid.measureddata.domain.repository.QuantityDetailRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.dominikbilik.smartgrid.measureddata.internal.StringUtils.valueOf;

@Service
public class DataSetService {

    private static final Logger LOG = LoggerFactory.getLogger(DataSetService.class);

    private QuantityDetailRepository quantityDetailRepository;
    private DataSetRepository dataSetRepository;

    @Autowired
    public DataSetService(QuantityDetailRepository quantityDetailRepository,
                          DataSetRepository dataSetRepository) {
        this.quantityDetailRepository = quantityDetailRepository;
        this.dataSetRepository = dataSetRepository;
    }

    @Transactional
    public DeviceDataSet checkAndCreateDataset(SingleValuesMeasurement<ObisSingleMeasurementRecord> measurementDto, Long deviceId) {
        LOG.info("checkAndCreateDataset called for MultiValuesMeasurement [fileId={}, deviceId={}]", measurementDto.getFileId(), deviceId);
        filterRecords(measurementDto);

        List<QuantityDetail> quantityDetails = new ArrayList<>();
        boolean allQuantityDetailsAlreadyExists = findQuantityDetails(quantityDetails, measurementDto);

        DeviceDataSet dataset = null;
        if (allQuantityDetailsAlreadyExists) {
            // find out, if dataset with specified quantitydetails already exists
            dataset = findExistingDataset(deviceId, quantityDetails);
        }

        if (dataset == null) {
            dataset = createNewDataset(measurementDto, deviceId, quantityDetails);
        }

        return dataset;
    }

    @Transactional
    public DeviceDataSet checkAndCreateDataset(MultiValuesMeasurement measurementDto, Long deviceId) {
        LOG.info("checkAndCreateDataset called for MultiValuesMeasurement [fileId={}, deviceId={}]", measurementDto.getFileId(), deviceId);
        String[] names = measurementDto.getQuantityNames();
        String[] units = measurementDto.getQuantityUnits() != null ? measurementDto.getQuantityUnits() : new String[names.length];
        Assert.notNull(names, "names can not be null !!!");
        //Assert.notNull(units, "Units can not be null !!!"); -> can be null for Meteo
        Assert.isTrue(names.length == units.length || units == null, "Names length " + names.length + " and units length " + units.length + " are not the same !!!");

        Map<String, String> nameUnitMap = new LinkedHashMap<>();
        IntStream.range(0, names.length)
                .forEach(i -> nameUnitMap.put(names[i], units[i]));
        LOG.info("Created map of names-units: {}", nameUnitMap);
        List<QuantityDetail> quantityDetails = new ArrayList<>();
        boolean newQuantityDetailFound = false;

        if (measurementDto.getMeasurementType().name().toUpperCase().contains("OBIS")) {
            for (Map.Entry<String, String> entry : nameUnitMap.entrySet()) {
                String[] tuple = entry.getKey().split("\\.");

                QuantityDetail detail = quantityDetailRepository.findByObisCode(
                        entry.getValue(),
                        null,
                        null,
                        tuple[0],
                        tuple[1],
                        null,
                        null
                );
                if (detail == null) {
                    LOG.info("Quantity Detail for [unit={}, measurementVariable={}, measurementType={}] not found. Creating new", entry.getValue(), tuple[0], tuple[1]);
                    detail = quantityDetailRepository.save(new QuantityDetail(entry.getValue(), tuple[0], tuple[1]));
                    newQuantityDetailFound = true;
                } else {
                    LOG.info("Quantity detail FOUND based on [name={}, unit={}], id={}", entry.getKey(), entry.getValue(), detail.getId());
                }

                quantityDetails.add(detail);
            }
        } else {
            for (Map.Entry<String, String> entry : nameUnitMap.entrySet()) {
                QuantityDetail quantityDetail = quantityDetailRepository.findByNameAndUnit(entry.getKey(), entry.getValue());

                if (quantityDetail == null) {
                    LOG.info("Quantity Detail for [name={}, unit={}] not found. Creating new", entry.getKey(), entry.getValue());
                    quantityDetail = quantityDetailRepository.save(new QuantityDetail(entry.getKey(), entry.getValue(), false));
                    newQuantityDetailFound = true;
                } else {
                    LOG.info("Quantity detail FOUND based on [name={}, unit={}], id={}", entry.getKey(), entry.getValue(), quantityDetail.getId());
                }

                quantityDetails.add(quantityDetail);
            }
        }

        DeviceDataSet dataset = null;
        if (!newQuantityDetailFound) {
            // find out, if dataset with specified quantitydetails already exists
            dataset = findExistingDataset(deviceId, quantityDetails);
        }

        if (dataset == null) {
            dataset = createNewDataset(measurementDto, deviceId, quantityDetails);
        }

        return dataset;
    }

    private DeviceDataSet findExistingDataset(Long deviceId, List<QuantityDetail> quantityDetails) {
        DeviceDataSet dataset = null;
        List<DeviceDataSet> datasets = dataSetRepository.findAllByReferenceDeviceId(deviceId);

        if (CollectionUtils.isNotEmpty(datasets)) {
            List<Long> quantityIds = quantityDetails.stream()
                    .map(detail -> detail.getId())
                    .collect(Collectors.toList());

            for(DeviceDataSet datasetIter : datasets) {
                if (CollectionUtils.isEqualCollection(quantityIds, datasetIter.getQuantityDetailIds())) {
                    dataset = datasetIter;
                    LOG.info("found existing dataset[{}] for deviceId {}", deviceId, datasetIter.getId());
                    break;
                }
            }

        } else {
            LOG.info("There is not existing dataset for deviceId {} with quantities {}", deviceId, quantityDetails);
        }

        return dataset;
    }

    private DeviceDataSet createNewDataset(MeasurementDto measurementDto, Long deviceId, List<QuantityDetail> quantityDetails) {
        LOG.info("Creating new dataset");
        // if we registered new quantitydetail, dataset does not exists for sure
        DeviceDataSet dataset = dataSetRepository.save(new DeviceDataSet(
                deviceId,
                measurementDto.getDeviceId(),
                measurementDto.getDeviceName(),
                measurementDto.getMeasurementType().name(),
                quantityDetails,
                (measurementDto instanceof MultiValuesMeasurement) ? ((MultiValuesMeasurement) measurementDto).getFrequencyInMinutes()/60 : 0));
        LOG.info("New dataset created: {}", dataset);

        return dataset;
    }

    /**
     * filter previous measurement records -> ones, where is F value, e.g. "1.6.0*99(00.00*kW)(0000000000000)"
     */
    public void filterRecords(SingleValuesMeasurement<ObisSingleMeasurementRecord> measurement) {
        List<ObisSingleMeasurementRecord> filteredRecords = new ArrayList<>();

        for (ObisSingleMeasurementRecord record : measurement.getRecords()) {
            if (record.getPreviousMeasurement() != null) {
                filteredRecords.add(record);
            }
        }
        LOG.info("Found {} previous measurement records", filteredRecords.size());
        measurement.getRecords().removeAll(filteredRecords);
    }

    /**
     *
     * @param quantityDetails - list of details which will be filled
     * @param measurementDto
     * @return boolean flag indicating if all Quantitydetails already exists(true) or new was created|(false)
     */
    public boolean findQuantityDetails(List<QuantityDetail> quantityDetails, SingleValuesMeasurement<ObisSingleMeasurementRecord> measurementDto) {
        boolean newQuantityDetailFound = true;
        for (ObisSingleMeasurementRecord record : measurementDto.getRecords()) {
            LOG.info("Pre Single: unit={}, medium={}, channel={}, variable={}, type={}, tariff={}, previous={}", record.getUnit(),
                    record.getMedium(),
                    valueOf(record.getChannel()),
                    valueOf(record.getMeasurementVariable()),
                    valueOf(record.getMeasurementType()),
                    valueOf(record.getTariff()),
                    valueOf(record.getPreviousMeasurement()));
            QuantityDetail detail = quantityDetailRepository.findByObisCode(
                    record.getUnit(),
                    valueOf(record.getMedium()),
                    valueOf(record.getChannel()),
                    valueOf(record.getMeasurementVariable()),
                    valueOf(record.getMeasurementType()),
                    valueOf(record.getTariff()),
                    valueOf(record.getPreviousMeasurement())
            );
            // quantity was not found , so lets create new -> need to save both normal and obis (obis with same ID)
            if (detail == null) {
                LOG.info("Quantity Detail not found (Creating new) for record {}", record);
                detail = quantityDetailRepository.save(new QuantityDetail(
                        record.getUnit(),
                        true,
                        valueOf(record.getMedium()),
                        valueOf(record.getChannel()),
                        valueOf(record.getMeasurementVariable()),
                        valueOf(record.getMeasurementType()),
                        valueOf(record.getTariff()),
                        valueOf(record.getPreviousMeasurement())
                ));
                newQuantityDetailFound = false;
            } else {
                LOG.info("Quantity detail FOUND based on record {}, quantityDetailId={}", record, detail.getId());
            }
            quantityDetails.add(detail);
        }

        return newQuantityDetailFound;
    }

}
