package com.dominikbilik.smartgrid.measureddata.service;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.search.FindRecordsRequest;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.search.FoundRecords;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.search.Quantity;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.search.SearchRecord;
import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import com.dominikbilik.smartgrid.measureddata.domain.entity.Record;
import com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet.DeviceDataSet;
import com.dominikbilik.smartgrid.measureddata.domain.repository.DataSetRepository;
import com.dominikbilik.smartgrid.measureddata.domain.repository.QuantityDetailRepository;
import com.dominikbilik.smartgrid.measureddata.domain.repository.RecordRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(RecordSearchService.class);

    private RecordRepository recordRepository;
    private DataSetRepository dataSetRepository;
    private QuantityDetailRepository quantityDetailRepository;

    @Autowired
    public RecordSearchService(RecordRepository recordRepository,
                               DataSetRepository dataSetRepository,
                               QuantityDetailRepository quantityDetailRepository) {
        this.recordRepository = recordRepository;
        this.dataSetRepository = dataSetRepository;
        this.quantityDetailRepository = quantityDetailRepository;
    }

    public List<FoundRecords> findRecords(FindRecordsRequest request) {
        List<FoundRecords> foundRecords = new ArrayList<>();
        List<DeviceDataSet> datasets = dataSetRepository.findAllByReferenceDeviceId(request.getDeviceId());
        if (CollectionUtils.isEmpty(datasets)) {
            throw new RuntimeException("No dataset exists for deviceId: " + request.getDeviceId());
        }

        for (Quantity searchQuantity : request.getQuantities()) {
            QuantityDetail quantityDetail;
            List<QuantityDetail> details = new ArrayList<>();
            quantityDetailRepository.findAllBySearchCriteria(
                    searchQuantity.getName(),
                    searchQuantity.getMedium(),
                    searchQuantity.getChannel(),
                    searchQuantity.getMeasurementVariable(),
                    searchQuantity.getMeasurementType(),
                    searchQuantity.getTariff(),
                    searchQuantity.getPreviousMeasurement()
            ).forEach(details::add);

            if (CollectionUtils.isEmpty(details)) {
                foundRecords.add(new FoundRecords("No Quantity detail found for " + searchQuantity));
                continue;
            } else if (details.size() > 1) {
                foundRecords.add(new FoundRecords("Too many Quantity details found for " + searchQuantity + ". Found: " + details));
                continue;
            } else {
                quantityDetail = details.get(0);
                LOG.info("Found QuantityDetail: {}", quantityDetail);
            }

            LOG.info("Found QuantityDetail: {}", quantityDetail);
            DeviceDataSet usingDataset = findDatasetForQuantity(datasets, quantityDetail);
            if (usingDataset == null) {
                foundRecords.add(new FoundRecords("None of found datasets contains quantityDetail " + quantityDetail));
                continue;
            }
            LOG.info("Found dataset {}", usingDataset);

            Instant start = Instant.now();
            List<Record> records = recordRepository.getRecords(request.getFrom(), request.getTo(), usingDataset.getId(), quantityDetail.getId());
            Instant end = Instant.now();
            LOG.info("Found {} records [from={}, to={}] in {}", records.size(), request.getFrom(), request.getTo(), Duration.between(start, end));

            if (CollectionUtils.isEmpty(records)) {
                foundRecords.add(new FoundRecords("No records found for [" + quantityDetail + "] in time between " + request.getFrom() + " and " + request.getTo()));
                continue;
            }

            foundRecords.add(new FoundRecords(
                    searchQuantity,
                    records.stream()
                            .map(record -> new SearchRecord(record.getDateTimeFrom(), record.getDateTimeTo(), record.getValue()))
                            .collect(Collectors.toList())));
        }

        return foundRecords;
    }

    private DeviceDataSet findDatasetForQuantity(List<DeviceDataSet> datasets, QuantityDetail quantityDetail) {
        return datasets.stream()
                .filter(dataset -> dataset.getQuantityDetailIds().contains(quantityDetail.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * If records were joined, we need to separate them back
     */
    private List<SearchRecord> divideRecordsIfJoined(List<Record> records, long frequencyInSeconds) {
        List<SearchRecord> divided = new ArrayList<>();
        for (Record record : records) {
            if (record.getDateTimeFrom() != null && record.getDateTimeTo() != null && !record.getDateTimeFrom().equals(record.getDateTimeTo())) {
                LocalDateTime tempDate = record.getDateTimeFrom();

                for (int i = 0; tempDate.isAfter(record.getDateTimeTo()); i++) {

                }
            }
            divided.add(new SearchRecord(record.getDateTimeFrom(), record.getDateTimeTo(), record.getValue()));
        }

        return divided;
    }
}
