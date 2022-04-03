package com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement;

import java.util.function.Supplier;

public enum NewMeasurementState implements SagaState<NewMeasurementState> {
    ERROR_OCCURED(() -> null),
    COMPLETED(() -> null),
    MEASUREMENT_PROCESSED(() -> NewMeasurementState.COMPLETED),
    DEVICE_VERIFIED(() -> NewMeasurementState.MEASUREMENT_PROCESSED),
    FILE_PROCESSSED(() -> NewMeasurementState.DEVICE_VERIFIED),
    STARTED(() -> NewMeasurementState.FILE_PROCESSSED);

    private final Supplier<NewMeasurementState> supplier;

    NewMeasurementState(Supplier<NewMeasurementState> supplier) {
        this.supplier = supplier;
    }

    @Override
    public NewMeasurementState nextState() {
        return supplier.get();
    }

    @Override
    public NewMeasurementState state() {
        return this;
    }

    @Override
    public NewMeasurementState initialState() {
        return NewMeasurementState.STARTED;
    }

    @Override
    public NewMeasurementState endingState() {
        return NewMeasurementState.COMPLETED;
    }

}
