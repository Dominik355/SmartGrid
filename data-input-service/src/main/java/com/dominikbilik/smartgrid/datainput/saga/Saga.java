package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.datainput.saga.objects.SagaStep;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.SagaState;

import java.util.List;

public abstract class Saga {

    protected SagaState state;

    public abstract List<SagaStep> getSteps();

    public SagaState getState() {
        return state;
    }

    public void updateState(SagaState state) {
        this.state = state;
    }

}
