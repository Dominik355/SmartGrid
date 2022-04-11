package com.dominikbilik.smartgrid.measureddata.service;

import com.dominikbilik.smartgrid.measureddata.api.rest.FileServiceProxy;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MeasurementDto;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.MultiValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.SingleValuesMeasurement;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.ObisSingleMeasurementRecord;
import com.dominikbilik.smartgrid.measureddata.domain.converters.MeasurementConverter;
import com.dominikbilik.smartgrid.measureddata.domain.entity.Measurement;
import com.dominikbilik.smartgrid.measureddata.domain.entity.QuantityDetail;
import com.dominikbilik.smartgrid.measureddata.domain.entity.Record;
import com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet.DeviceDataSet;
import com.dominikbilik.smartgrid.measureddata.domain.repository.MeasurementRepository;
import com.dominikbilik.smartgrid.measureddata.domain.repository.QuantityDetailRepository;
import com.dominikbilik.smartgrid.measureddata.domain.repository.RecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private FileServiceProxy fileServiceProxy;
    private MeasurementRepository measurementRepository;
    private DataSetService dataSetService;
    private QuantityDetailRepository quantityDetailRepository;
    private RecordRepository recordRepository;

    @Autowired
    public ProcessingService(FileServiceProxy fileServiceProxy,
                             MeasurementRepository measurementRepository,
                             DataSetService dataSetService,
                             QuantityDetailRepository quantityDetailRepository,
                             RecordRepository recordRepository) {
        this.fileServiceProxy = fileServiceProxy;
        this.measurementRepository = measurementRepository;
        this.dataSetService = dataSetService;
        this.quantityDetailRepository = quantityDetailRepository;
        this.recordRepository = recordRepository;
    }

    public Long processMeasurement(Long fileId, Long deviceId, String measurementType) {
        LOG.info("ProcessingService.processMeasurement(fileId={}, deviceId={}, measurementType={}", fileId, deviceId, measurementType);
        Assert.notNull(fileId, "fileId can not be null !!!");
        Assert.notNull(deviceId, "deviceId can not be null !!!");
        MeasurementDto measurementDto = null;
        try {
            measurementDto = fileServiceProxy.getMeasurementFromParsedFile(fileId, measurementType)
                    .orElseThrow(() -> new RuntimeException("Returned measurement is null"));
        } catch (Exception ex) {
            LOG.info("were not able to obtain parsed measurement from file-service");
            ex.printStackTrace();
            return null;
        }

        return processMeasurement(measurementDto, deviceId);
    }

    // vytvorit a ulozit measurement
    // ziskat dataset
    // ulozit jednotlive record po stlpcoch
    // updatnut records view (udrziavanie udajov za poslednych 24 hodin)
    @Transactional
    public Long processMeasurement(MeasurementDto measurementDto, Long deviceId) {
        LOG.info("ProcessingService.processMeasurement(measurement={}, deviceId={}", measurementDto, deviceId);
        Assert.notNull(measurementDto, "Measurement can not be null");
        Assert.notNull(deviceId, "deviceId can not be null !!!");

        Measurement measurement = measurementRepository.save(MeasurementConverter.dtoToEntity(measurementDto, deviceId));

        DeviceDataSet dataset;
        long count = 0;
        if (measurementDto instanceof SingleValuesMeasurement) {
            SingleValuesMeasurement<ObisSingleMeasurementRecord> singleMeasurement = (SingleValuesMeasurement<ObisSingleMeasurementRecord>) measurementDto;
            dataset = dataSetService.checkAndCreateDataset(singleMeasurement, deviceId);

            List<QuantityDetail> details = new ArrayList<>();
            quantityDetailRepository.findAllById(dataset.getQuantityDetailIds()).forEach(details::add);

            count = saveSingleValuesMeasurementRecords(((SingleValuesMeasurement<ObisSingleMeasurementRecord>) measurementDto).getRecords(), details, dataset.getId());

        } else if (measurementDto instanceof MultiValuesMeasurement) {
            dataset = dataSetService.checkAndCreateDataset((MultiValuesMeasurement) measurementDto, deviceId);

            List<QuantityDetail> details = new ArrayList<>();
            quantityDetailRepository.findAllById(dataset.getQuantityDetailIds()).forEach(details::add);
            details = orderQuantityDetailsAccordingToColumns((MultiValuesMeasurement) measurementDto, details);

            count = saveMultiValuesMeasurementRecords(((MultiValuesMeasurement) measurementDto).getRecords(), details, dataset.getId());
        } else {
            throw new RuntimeException("Could not identifiy instance for measurementDto: " + measurementDto);
        }
        LOG.info("Saved {} new records", count);
        return count;
    }

    private QuantityDetail findQuantityDetail(ObisSingleMeasurementRecord record, List<QuantityDetail> details) {
        for (QuantityDetail detail : details) {
            if (detail.getUnit().equals(record.getUnit())
                    && detail.getMedium().equals(String.valueOf(record.getMedium()))
                    && detail.getChannel().equals(String.valueOf(record.getChannel()))
                    && detail.getMeasurementVariable().equals(String.valueOf(record.getMeasurementVariable()))
                    && detail.getMeasurementType().equals(String.valueOf(record.getMeasurementType()))
                    && detail.getTariff().equals(String.valueOf(record.getTariff()))
                    && detail.getPreviousMeasurement().equals(String.valueOf(record.getPreviousMeasurement()))) {
                LOG.info("Found detail {} for record {}", detail, record);
                details.remove(detail);
                return detail;
            }
        }
        LOG.info("QuantityDetail not found for record {}", record);
        return null;
    }

    /**
     * QuantityDetails should be already ordered, cause we were adding them one be one in order into ArrayList from linkedHashMap
     * But to be 100% sure..
     */
    private List<QuantityDetail> orderQuantityDetailsAccordingToColumns(MultiValuesMeasurement measurementDto, List<QuantityDetail> details) {
        LOG.info("Ordering quantityDetails, {}", details);
        List<QuantityDetail> detailsToReturn = new ArrayList<>();
        String[] names = measurementDto.getQuantityNames();
        String[] units = measurementDto.getQuantityUnits() != null ? measurementDto.getQuantityUnits() : new String[names.length];
        boolean isObis = measurementDto.getMeasurementType().name().toUpperCase().contains("OBIS");

        for (int i = 0; i < names.length; i++) {
            for (QuantityDetail detail : details) {
                if (isObis) {
                    String[] tuple = names[i].split("\\.");
                    if (detail.getUnit().equals(units[i])
                        && detail.getMeasurementVariable().equals(tuple[0])
                        && detail.getMeasurementType().equals(tuple[1])) {
                        detailsToReturn.add(detail);
                        break;
                    }
                } else {
                    if (detail.getUnit() != null) {
                        if (detail.getUnit().equals(units[i]) && detail.getName().equals(names[i])) {
                            detailsToReturn.add(detail);
                            details.remove(detail);
                            break;
                        }
                    } else {
                        if (detail.getName().equals(names[i])) {
                            detailsToReturn.add(detail);
                            details.remove(detail);
                            break;
                        }
                    }
                }
            }

        }
        return detailsToReturn;
    }

    private long saveSingleValuesMeasurementRecords(List<ObisSingleMeasurementRecord> records, List<QuantityDetail> quantityDetails, Long datasetId) {
        long count = 0;
        Instant startOverall = Instant.now();
        for (int i = 0; i < records.size(); i++) {
            ObisSingleMeasurementRecord processedRecord = records.get(i);
            QuantityDetail quantityDetail = findQuantityDetail(processedRecord, quantityDetails);

            if(quantityDetail == null && processedRecord.getPreviousMeasurement() != null) {
                LOG.info("QuantityDetail not found for record: {}", processedRecord);
                continue;
            }

            count += recordRepository.save(new Record(processedRecord.getValue(),
                    processedRecord.getDateTime(),
                    processedRecord.getDateTime(),
                    datasetId,
                    quantityDetail.getId())
            );
        }
        Instant endOverall = Instant.now();
        LOG.info("Overall time of processing : {}", Duration.between(startOverall, endOverall));

        return count;
    }

    private long saveMultiValuesMeasurementRecords(List<MultiMeasurementRecord> records, List<QuantityDetail> quantityDetails, Long datasetId) {
        LOG.info("saveMultiValuesMeasurementRecords called records={}, quantityDetails={}", records.size(), quantityDetails);
        long count = 0;
        int columnNum = records.get(0).getValues().length;
        Assert.isTrue(columnNum == quantityDetails.size(), "columnNum[" + columnNum + "] and quantitiDetailsNum[" + quantityDetails.size() + "] is not equal");

        // if last row is incomplete, delete it (file could be taken before measurement ended so data is incomplete)
        if (records.get(records.size() - 1).getValues().length != columnNum) {
            records.remove(records.size() - 1);
        }

        Instant startOverall = Instant.now();
        for (int col = 0; col < columnNum; col++) {
            LOG.info("Going over column {} out of {}", col, columnNum - 1);
            QuantityDetail actualQD = quantityDetails.get(col);
            Instant start = Instant.now();
            /*
            for (int row = 0; row < records.size(); row++) {
                count += recordRepository.save(new Record(records.get(row).getValues()[col],
                        records.get(row).getDateTime(),
                        records.get(row).getDateTime(),
                        datasetId,
                        actualQD.getId())
                );
            }
            */
            List<Record> recordsToInsert = new ArrayList<>();
            for (int row = 0; row < records.size(); row++) {
                recordsToInsert.add(new Record(records.get(row).getValues()[col],
                        records.get(row).getDateTime(),
                        records.get(row).getDateTime(),
                        datasetId,
                        actualQD.getId())
                );
            }

            int[] saved = recordRepository.batchInsert(recordsToInsert);
            count += saved.length;
            Instant end = Instant.now();
            LOG.info("count {}. Column {} out of {} stored in: {}", count, col, columnNum - 1, Duration.between(start, end));
        }
        Instant endOverall = Instant.now();
        LOG.info("Overall time of processing : {}", Duration.between(startOverall, endOverall));
        return count;
    }

}
