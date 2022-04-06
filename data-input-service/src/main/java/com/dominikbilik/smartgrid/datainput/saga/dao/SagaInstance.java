package com.dominikbilik.smartgrid.datainput.saga.dao;

import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "saga_instance")
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String sagaType;
    @Column(nullable = false)
    private String sagaSystemId; // sagaId used accross services as correlationID and message Key
    @ElementCollection
    @CollectionTable(name = "saga_steps", joinColumns = @JoinColumn(name = "saga_id"))
    private List<SagaStepInstance> steps;
    private LocalDateTime creationTime;
    private LocalDateTime finishTime;
    private Boolean completed;
    private String unsuccesfulCompensations;
    // last state succesfuly executed
    private String endState;
    private LocalDateTime errorTime;
    private Integer errorStep;
    // String representation of the json object that presents the resulting state
    private String resultValue;

    public SagaInstance() {}

    public SagaInstance(String sagaType, LocalDateTime creationTime, String state, String sagaSystemId) {
        this.sagaType = sagaType;
        this.creationTime = creationTime;
        this.endState = state;
        this.sagaSystemId = sagaSystemId;
    }

    public Long getId() {
        return id;
    }

    public String getSagaType() {
        return sagaType;
    }

    public String getSagaSystemId() {
        return sagaSystemId;
    }

    public List<SagaStepInstance> getSteps() {
        return steps;
    }

    public void setSteps(List<SagaStepInstance> steps) {
        this.steps = steps;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getUnsuccesfulCompensations() {
        return unsuccesfulCompensations;
    }

    public void setUnsuccesfulCompensations(String unsuccesfulCompensations) {
        this.unsuccesfulCompensations = unsuccesfulCompensations;
    }

    public String getEndState() {
        return endState;
    }

    public void setEndState(String endState) {
        this.endState = endState;
    }

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }

    public Integer getErrorStep() {
        return errorStep;
    }

    public void setErrorStep(Integer errorStep) {
        this.errorStep = errorStep;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public void addStep(SagaStepInstance step) {
        if (CollectionUtils.isEmpty(this.steps)) {
            this.steps = new ArrayList<>();
        }
        this.steps.add(step);
    }
}
