package com.dominikbilik.smartgrid.measureddata.service;

import com.dominikbilik.smartgrid.measureddata.api.v1.dto.measurements.Measurement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private JdbcAggregateTemplate template;

    @Autowired
    public ProcessingService(JdbcAggregateTemplate template) {
        this.template = template;
    }

    // vytvorit a ulozit measurement -> ten zakladny (alebo az neskor cely ?)
    // vytvorit a ulozit measurement headers -> cez aggregate
    // skontrolovat, ci existuje DataSet, ak nie, tak vytvorit novy a ulozit
    // nalinkovat dataset a ulozit -> az teraz ulozit measurement, alebo iba urobit update ?
    // ulozit info
    // ulozit records
    // updatnut records view (udrziavanie udajov za poslednych 24 hodin)
    @Transactional
    public <T extends Measurement> void processMeasurement(T measurementDto) {

    }


}
