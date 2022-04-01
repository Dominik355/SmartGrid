package com.dominikbilik.smartgrid.datainput.saga.objects;

import com.dominikbilik.smartgrid.datainput.saga.participants.Command;

import java.util.function.Function;

public class SagaStep<Data> {

    private int orderNumber;
    private Function<? extends Command, ? extends Object> action;
    private Function<? extends Command, ? extends Object> compenstation;

    public SagaStep() {

    }
}
