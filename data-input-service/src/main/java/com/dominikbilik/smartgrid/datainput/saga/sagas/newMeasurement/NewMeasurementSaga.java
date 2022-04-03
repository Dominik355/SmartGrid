package com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement;

import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.datainput.saga.Saga;
import com.dominikbilik.smartgrid.datainput.saga.objects.SagaStep;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommandResponse;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommandResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewMeasurementSaga extends Saga {

    private NewMeasurementSagaState sagaState;
    private List<SagaStep> steps;

    public NewMeasurementSaga(NewMeasurementHandler handler) {
        List<SagaStep> steps = new ArrayList<>();

        steps.add(
                new SagaStep.SagaStepBuilder<ProcessFileCommandResponse, NewMeasurementSagaState, NewMeasurementState>()
                        .action(handler::processFile, NewMeasurementSagaState::createProcessFileCommand)
                        .onReply(NewMeasurementSagaState::handleProcessFileCommandResponse)
                        .compenstation(handler::unprocessFile, NewMeasurementSagaState::createUnprocessFileCommand)
                        .resultState(NewMeasurementState.FILE_PROCESSSED)
                        .build()
        );

        steps.add( // it's just veryfying, no need for compensation
                new SagaStep.SagaStepBuilder<VerifyDeviceCommandResponse, NewMeasurementSagaState, NewMeasurementState>()
                        .action(handler::verifyDevice, NewMeasurementSagaState::createVerifyDeviceCommand)
                        .onReply(NewMeasurementSagaState::handleVerifyDeviceCommandResponse)
                        .resultState(NewMeasurementState.DEVICE_VERIFIED)
                        .build()
        );

        steps.add( // we don't need compensation here, cause it's a last step
                new SagaStep.SagaStepBuilder<ProcessMeasurementCommandResponse, NewMeasurementSagaState, NewMeasurementState>()
                        .action(handler::processMeasurement, NewMeasurementSagaState::createProcessMeasurementCommand)
                        .onReply(NewMeasurementSagaState::handleProcessMeasurementCommandResponse)
                        .resultState(NewMeasurementState.MEASUREMENT_PROCESSED)
                        .build()
        );

        checkStatesSequence(steps);
        this.steps = steps;
    }


    public void start() {
        Assert.isTrue(CollectionUtils.isNotEmpty(getSteps()), "Steps can not be empty !!!");
        sagaState = new NewMeasurementSagaState("saga-id-test-11111", "filename_Test", LocalDateTime.now());

        System.out.println("state before: " + sagaState);
        for (SagaStep<ProcessMeasurementCommandResponse, NewMeasurementSagaState, NewMeasurementState> step : getSteps()) {

            MessageSupplier messageSupplier = step.getCommandSupplier().apply(sagaState);
            ProcessMeasurementCommandResponse response = step.getAction().apply(messageSupplier);
            step.getReplyProcessor().accept(sagaState, response);

            break;
        }

        System.out.println("state after: " + sagaState);
    }


    @Override
    public List<SagaStep> getSteps() {
        return steps;
    }

    public void checkStatesSequence(List<SagaStep> sagaSteps) {
        for (int i = 0; i < sagaSteps.size(); i++) {
            SagaState<NewMeasurementState> currentState = sagaSteps.get(i).getResultState();

            if (i == 0) {
                NewMeasurementState startingState = currentState.initialState();
                // if first result state defined in steps is not nextStep of starting step -> exception
                if (!startingState.nextState().equals(currentState)) {
                    throw new RuntimeException(
                            String.format("First step in defined steps can not be first in this Saga.[Defined step state = %s, Expected step state = %s]",
                                    currentState.state().name(),
                                    startingState.name()));
                }
                continue;
            }
            if (i == sagaSteps.size() - 1) {
                // if last result state defined in steps has not last step as his next step -> exception
                if (currentState.state().nextState().nextState() != null) {
                    throw new RuntimeException(
                            String.format("Last step in defined steps is not followed by ending state.[Defined step state = %s]", currentState.state().name()));
                }
                continue;
            }

            // ok, so we are not dealing with first nor last step in saga, so let's just check next step
            SagaState<NewMeasurementState> nextState = sagaSteps.get(i+1).getResultState();
            if (currentState.nextState() != nextState.state()) {
                throw new RuntimeException(
                        String.format("Steps are not in correct order.[Analyzed state = %s. His next state = %s, expected next state = %s]",
                                currentState.state().name(),
                                currentState.nextState().name(),
                                nextState.state().name()));
            }
        }
    }
}
