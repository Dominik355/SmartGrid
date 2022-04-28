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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.dominikbilik.smartgrid.measureddata.internal.StringUtils.valueOf;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final long RECORD_TOLERANCE_MINUTES = 2;

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
                    && detail.getMedium() != null ? detail.getMedium().equals(valueOf(record.getMedium())) : true
                    && detail.getChannel() != null ? detail.getChannel().equals(valueOf(record.getChannel())) : true
                    && detail.getMeasurementVariable() != null ? detail.getMeasurementVariable().equals(valueOf(record.getMeasurementVariable())) : true
                    && detail.getMeasurementType() != null ? detail.getMeasurementType().equals(valueOf(record.getMeasurementType())) : true
                    && detail.getTariff() != null ? detail.getTariff().equals(valueOf(record.getTariff())) : true
                    && detail.getPreviousMeasurement() != null ? detail.getPreviousMeasurement().equals(valueOf(record.getPreviousMeasurement())) : true) {
                LOG.info("Found detail {} for record {}", detail, record);
                details.remove(detail);
                return detail;
            }
        }
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

            if(quantityDetail == null) {
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

        Collections.sort(records, Comparator.comparing(com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.MultiMeasurementRecord::getDateTime));

        Instant startOverall = Instant.now();
        for (int col = 0; col < columnNum; col++) {
            LOG.info("Going over column {} out of {}", col, columnNum - 1);
            QuantityDetail actualQD = quantityDetails.get(col);
            Instant start = Instant.now();
/*          ONE BY ONE
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
            Record record = null;

            // find last record before this measurement. First record is oldest, so find first up to that date
            Record lastRecord = recordRepository.getLastRecordToDefinedTime(datasetId,  actualQD.getId(), records.get(0).getDateTime());
            if (lastRecord == null) {
                LOG.info("the previous record was not found [datasetId={}, quantityId={}, time={}]", datasetId,  actualQD.getId(), records.get(0).getDateTime());
            } else if (lastRecord.getDateTimeTo().plusMinutes(RECORD_TOLERANCE_MINUTES).isBefore(records.get(0).getDateTime())) {
                LOG.info("Last found record is not new enought to be connected with first record of measurement [lastRecord timeTo={}, new record time from={}]", lastRecord.getDateTimeTo(), (records.get(0).getDateTime()));
            } else {
                LOG.info("Candidate record for join found [lastRecord timeTo={}, new record time from={}]", lastRecord.getDateTimeTo(), (records.get(0).getDateTime()));
                if (!records.get(0).getValues()[col].equals(lastRecord.getValue())) {
                    LOG.info("Hodnoty sa nezhoduju, mozeme ist dalej");
                } else {
                    LOG.info("Nasli sme zhodu v case aj hodnotach, hodnota = {}. Nastavime record", lastRecord.getValue());
                    record = lastRecord;
                }
            }

            int row = 0;
            if (record != null) {
                LOG.info("record nieje null, tak ideme najst, pokial ide aj snura rovnakych hodnot");
                // pokial record nieje null, tak hladame dokedy je zhoda, a potom record nie insertneme ale updatneme, nasledne sa pojdu robit klasicke inserty
                for (; row < records.size(); row++) {
                    Double actualValue = records.get(row).getValues()[col];
                    if (record.getValue().equals(actualValue)) {
                        LOG.info("nasli sme zhodu v hodnote {}, pre riadok {}", actualValue, row);
                        record.setDateTimeTo(records.get(row).getDateTime());
                    } else {
                        LOG.info("Snura rovnakych hodnot sa ukoncila na riadku {}, updatneme povodny record {} s novym casom DO {}, nastavime record na null", lastRecord, record.getDateTimeTo(), row);
                        recordRepository.updateDateTimeTo(lastRecord, record.getDateTimeTo());
                        record = null;
                        break;
                    }
                }
            }


            for (; row < records.size(); row++) {
                LOG.info("pokracujeme na riadku {}", row);
                Double actualValue = records.get(row).getValues()[col];

                if (record == null) {
                    LOG.info("Record je null, vytvorili sme novy pre row {}", row);
                    record = new Record(actualValue,
                            records.get(row).getDateTime(),
                            records.get(row).getDateTime(),
                            datasetId,
                            actualQD.getId());
                    continue;
                } else {
                    if (record.getValue().equals(actualValue)) {
                        LOG.info("Snura rovnakych hodnot pokracuje pre hodnotu {}, sme na riadku {}", actualValue, row);
                        record.setDateTimeTo(records.get(row).getDateTime());
                    } else {
                        LOG.info("Record nieje null, no hodnota uz sa nezhoduje[aktualna={}], preto ulozime co mame a nastavime novy record", actualValue);
                        recordsToInsert.add(record);

                        record = new Record(actualValue,
                                records.get(row).getDateTime(),
                                records.get(row).getDateTime(),
                                datasetId,
                                actualQD.getId());
                    }
                }

            }

            if (record != null) {
                LOG.info("For cyklus sa ukoncil, ulozime {}", record);
                recordsToInsert.add(record);
            }
/*
            for (int row = 0; row < records.size(); row++) {
                recordsToInsert.add(new Record(records.get(row).getValues()[col],
                        records.get(row).getDateTime(),
                        records.get(row).getDateTime(),
                        datasetId,
                        actualQD.getId())
                );
            }
*/
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
