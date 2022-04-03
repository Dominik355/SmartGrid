package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.datainput.saga.objects.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {
}
