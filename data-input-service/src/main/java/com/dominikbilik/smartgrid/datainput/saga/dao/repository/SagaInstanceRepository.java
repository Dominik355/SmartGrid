package com.dominikbilik.smartgrid.datainput.saga.dao.repository;

import com.dominikbilik.smartgrid.datainput.saga.dao.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {
}
