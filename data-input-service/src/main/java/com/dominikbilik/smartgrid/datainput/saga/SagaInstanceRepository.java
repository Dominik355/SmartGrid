package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.datainput.saga.objects.SagaInstance;

public interface SagaInstanceRepository {

    void save(SagaInstance sagaInstance);
    SagaInstance findBySagaTypeAndId(String sagaType, String id);

}
