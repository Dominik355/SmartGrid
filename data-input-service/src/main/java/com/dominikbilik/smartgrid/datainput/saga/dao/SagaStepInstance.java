package com.dominikbilik.smartgrid.datainput.saga.dao;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class SagaStepInstance {

    private int orderNumber;
    private String stepName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public SagaStepInstance() {}

    public SagaStepInstance(int orderNumber, String stepName, LocalDateTime startTime) {
        this.orderNumber = orderNumber;
        this.stepName = stepName;
        this.startTime = startTime;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
