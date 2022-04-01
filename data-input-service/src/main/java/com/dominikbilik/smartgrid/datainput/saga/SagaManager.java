package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.datainput.saga.objects.SagaInstance;
import com.dominikbilik.smartgrid.datainput.saga.objects.SagaStep;

import java.util.List;

public class SagaManager<Data> {

    private List<SagaStep<Data>> sagaSteps;

    public SagaManager(List<SagaStep<Data>> sagaSteps) {
        this.sagaSteps = sagaSteps;
    }

}
