package com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement;

import com.dominikbilik.smartgrid.common.model.MessageReply;
import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.datainput.saga.Saga;
import com.dominikbilik.smartgrid.datainput.saga.SagaStep;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewMeasurementSaga extends Saga {

    public static final String SAGA_NAME = "NEW_MEASUREMENT_SAGA";

    private NewMeasurementSagaState sagaState;
    private List<SagaStep> steps;

    @Override
    public String getSagaName() {
        return SAGA_NAME;
    }
    @Override
    public Object getData() {
        return sagaState;
    }

    @Override
    public String getSagaSystemId() {
        return this.sagaState.getSagaId();
    }

    @Override
    public SagaState executeAction(int stepNum) {
        SagaStep<NewMeasurementSagaState, NewMeasurementState> realStep = steps.get(stepNum);

        MessageSupplier messageSupplier = realStep.getCommandSupplier().apply(sagaState);
        MessageReply response = realStep.getAction().apply(messageSupplier);
        realStep.getReplyProcessor().accept(sagaState, response);

        return realStep.getResultState();
    }

    @Override
    public boolean executeCompensation(int stepNum) {
        SagaStep<NewMeasurementSagaState, NewMeasurementState> realStep = steps.get(stepNum);

        Boolean result = true;
        if (realStep.getReplyProcessor() != null) {
            MessageSupplier messageSupplier = realStep.getReverseCommandSupplier().apply(sagaState);
            result = realStep.getReverseAction().apply(messageSupplier);
        }

        return result.booleanValue();
    }


    public NewMeasurementSaga(NewMeasurementHandler handler, String sagaSystemId, String filename) {
        this.sagaState = new NewMeasurementSagaState(sagaSystemId, filename);

        List<SagaStep> steps = new ArrayList<>();

        steps.add( //ProcessFileCommandResponse
                new SagaStep.SagaStepBuilder<NewMeasurementSagaState, NewMeasurementState>("FILE_PROCESSING")
                        .action(handler::processFile, NewMeasurementSagaState::createProcessFileCommand)
                        .onReply(NewMeasurementSagaState::handleProcessFileCommandResponse)
                        .compenstation(handler::unprocessFile, NewMeasurementSagaState::createUnprocessFileCommand)
                        .resultState(NewMeasurementState.FILE_PROCESSSED)
                        .build()
        );

        steps.add( // it's just veryfying, no need for compensation //VerifyDeviceCommandResponse
                new SagaStep.SagaStepBuilder<NewMeasurementSagaState, NewMeasurementState>("DEVICE_VERIFICATION")
                        .action(handler::verifyDevice, NewMeasurementSagaState::createVerifyDeviceCommand)
                        .onReply(NewMeasurementSagaState::handleVerifyDeviceCommandResponse)
                        .resultState(NewMeasurementState.DEVICE_VERIFIED)
                        .build()
        );

        steps.add( // we don't need compensation here, cause it's a last step //ProcessMeasurementCommandResponse
                new SagaStep.SagaStepBuilder<NewMeasurementSagaState, NewMeasurementState>("MEASUREMENT_PROCESSING")
                        .action(handler::processMeasurement, NewMeasurementSagaState::createProcessMeasurementCommand)
                        .onReply(NewMeasurementSagaState::handleProcessMeasurementCommandResponse)
                        .resultState(NewMeasurementState.MEASUREMENT_PROCESSED)
                        .build()
        );

        this.steps = steps;
    }

    public void executeStep() {

    }

    @Override
    public List<SagaStep> getSteps() {
        return steps;
    }

}
