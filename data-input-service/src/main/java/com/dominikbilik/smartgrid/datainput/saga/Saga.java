package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.SagaState;

import java.util.List;

public abstract class Saga {

    public abstract String getSagaName();

    public abstract List<SagaStep> getSteps();

    public abstract Object getData();

    public abstract String getSagaSystemId();

    public abstract SagaState executeAction(int stepNum);

    public abstract boolean executeCompensation(int stepNum);

}
