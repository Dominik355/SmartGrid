package com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement;

public interface SagaState<T extends SagaState> {

    public T nextState();
    public T state();
    public T initialState();
    public T endingState();
    public String getName();

    public default String getErrorState() {
        return "ERROR_OCCURED";
    }

}
