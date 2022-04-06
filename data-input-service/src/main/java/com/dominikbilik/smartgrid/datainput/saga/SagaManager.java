package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.datainput.saga.dao.SagaInstance;
import com.dominikbilik.smartgrid.datainput.saga.dao.SagaStepInstance;
import com.dominikbilik.smartgrid.datainput.saga.dao.repository.SagaInstanceRepository;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.SagaState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SagaManager {

    private static final Logger LOG = LoggerFactory.getLogger(SagaManager.class);

    @Autowired
    private SagaInstanceRepository sagaInstanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public SagaInstance start(Saga saga) {
        Assert.notNull(saga, "Saga can not be null !!!");
        Assert.isTrue(CollectionUtils.isNotEmpty(saga.getSteps()), "Steps can not be empty !!!");
        LOG.debug("Starting to process new Saga of type {} with systemID {}", saga.getSagaName(), saga.getSagaSystemId());

        List<SagaStep> steps = saga.getSteps();
        checkStatesSequence(steps);

        SagaInstance sagaInstance = new SagaInstance(
                saga.getSagaName(),
                LocalDateTime.now(),
                steps.get(0).getResultState().initialState().getName(),
                saga.getSagaSystemId()
        );
        sagaInstanceRepository.save(sagaInstance);

        int numOfSteps = saga.getSteps().size();
        int stepIterator;
        boolean completed = false;
        for (stepIterator = 0; stepIterator < numOfSteps; stepIterator++) {
            int currentReadableStep = stepIterator+1;
            SagaStep currentStep = steps.get(stepIterator);
            LOG.info("Executing step {} named {} in Saga {}.", currentReadableStep, currentStep.getStepName(), saga.getSagaName());
            try {
                // execute step and update sagainstance endingstate
                SagaStepInstance sagaStepInstance = new SagaStepInstance(currentReadableStep, currentStep.getStepName(), LocalDateTime.now());

                SagaState state = saga.executeAction(stepIterator);

                sagaStepInstance.setEndTime(LocalDateTime.now());
                sagaInstance.addStep(sagaStepInstance);
                sagaInstance.setEndState(state.getName());
                sagaInstanceRepository.save(sagaInstance);
            } catch (Exception ex) {
                // exception occured, so, let's stop right here and execute compensating steps
                // because handling method returns void, any error or missing data or anything worth stoping saga has to throw an exception
                LOG.error("ERROR occured at step [order={}, name={}] of Saga {}. Compensating steps are about to execute",
                            currentReadableStep, currentStep.getStepName(), saga.getSagaName());
                ex.printStackTrace();
                break;
            }

            if (stepIterator == numOfSteps - 1) {
                LOG.info("Saga was successfuly completed");
                completed = true;
            }
        }

        if (!completed) {
            LOG.info("{} steps we executed, but error occured at last executed step", stepIterator);
            // so, not all steps executed, that means exception occured -> lets execute compensation
            executeCompensations(saga, sagaInstance, stepIterator);
        } else {
            LOG.info("Setting completed and end state parameter for saga instance");
            sagaInstance.setCompleted(Boolean.TRUE);
            sagaInstance.setEndState(saga.getSteps().get(0).getResultState().endingState().getName());
        }

        try {
            sagaInstance.setResultValue(objectMapper.writeValueAsString(saga.getData()));
        } catch (Exception e) {
            LOG.warn("Error occured while trying to convert object of saga data {} into json string", saga.getData());
        }

        sagaInstance.setFinishTime(LocalDateTime.now());
        return sagaInstanceRepository.save(sagaInstance);
    }

    private void executeCompensations(Saga saga, SagaInstance sagaInstance, int errorStep) {
        if (errorStep == 0) {
            LOG.info("Error occured at first step, there is nothing to compensate");
            return;
        }
        LOG.info("Compensation process for saga {} starting from step {}", saga.getSagaName(), errorStep - 1);
        sagaInstance.setCompleted(Boolean.FALSE);
        sagaInstance.setErrorTime(LocalDateTime.now());
        sagaInstance.setErrorStep(errorStep + 1);

        // e.g. error step = 3, we wanna compensate steps 2 to 0
        StringBuilder unsuccessfulCompensations = new StringBuilder();
        for (int i = errorStep - 1; i == 0; i--) {
            LOG.info("Compensating step {} for saga {}", errorStep + 1, saga.getSagaName());

            boolean compensationSucsessful = false;
            try {
                compensationSucsessful = saga.executeCompensation(i);
            } catch (Exception ex) {
                LOG.error("Compensating step {} unsuccessful", errorStep - 1);
            }

            if (!compensationSucsessful) {
                unsuccessfulCompensations.append(saga.getSteps().get(errorStep).getStepName() + ",");
            }
        }

        sagaInstance.setUnsuccesfulCompensations(unsuccessfulCompensations.toString());
    }

    public void checkStatesSequence(List<SagaStep> sagaSteps) {
        List<String> duplicationControlList = new ArrayList<>();
        for (int i = 0; i < sagaSteps.size(); i++) {
            SagaState currentState = sagaSteps.get(i).getResultState();

            if (duplicationControlList.contains(currentState.getName())) {
                throw new RuntimeException(
                        String.format("Found duplicity for state %s", currentState.state().getName()));
            }

            if (i == 0) {
                SagaState startingState = currentState.initialState();
                // if first result state defined in steps is not nextStep of starting step -> exception
                if (!startingState.nextState().equals(currentState)) {
                    throw new RuntimeException(
                            String.format("First step in defined steps can not be first in this Saga.[Defined step state = %s, Expected step state = %s]",
                                    currentState.state().getName(),
                                    startingState.getName()));
                }
            } else if (i == sagaSteps.size() - 1) {
                // if last result state defined in steps has not last step as his next step -> exception
                if (currentState.state().nextState() != currentState.endingState()) {
                    throw new RuntimeException(
                            String.format("Last step in defined steps is not followed by ending state.[Defined step state = %s]", currentState.state().getName()));
                }
            } else {
                // ok, so we are not dealing with first nor last step in saga, so let's just check next step
                SagaState nextState = sagaSteps.get(i+1).getResultState();
                if (currentState.nextState() != nextState.state()) {
                    throw new RuntimeException(
                            String.format("Steps are not in correct order.[Analyzed state = %s. His next state = %s, expected next state = %s]",
                                    currentState.state().getName(),
                                    currentState.nextState().getName(),
                                    nextState.state().getName()));
                }
            }
            // add state for duplication control
            duplicationControlList.add(currentState.getName());
        }
    }

}
