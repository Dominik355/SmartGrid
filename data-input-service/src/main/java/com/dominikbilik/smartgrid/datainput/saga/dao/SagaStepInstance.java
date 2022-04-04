package com.dominikbilik.smartgrid.datainput.saga.dao;

import javax.persistence.Embeddable;

@Embeddable
public class SagaStepInstance {

    private int orderNumber;
    private String stepName;

    public SagaStepInstance() {}

    public SagaStepInstance(int orderNumber, String stepName) {
        this.orderNumber = orderNumber;
        this.stepName = stepName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
}
