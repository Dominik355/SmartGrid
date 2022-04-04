package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.common.model.MessageReply;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.SagaState;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SagaStep<Data, State extends SagaState<State>> {

    private final String stepName;

    // creates MessageSupplier with message and kafka message key
    private final Function<Data, MessageSupplier> commandSupplier;
    // sends command and returns reply
    private final Function<MessageSupplier, MessageReply> action;
    // process reply and updates saga state and data
    private final BiConsumer<Data, MessageReply> replyProcessor;


    // creates MessageSupplier for reverse command
    private final Function<Data, MessageSupplier>  reverseCommandSupplier;
    // evaluate whether the message was sent successfully
    private final Function<MessageSupplier, Boolean> reverseAction;

    private final State resultState;

    public static class SagaStepBuilder<Data, State extends SagaState<State>> {
        private String stepName;

        private Function<Data, MessageSupplier> commandSupplier;
        private Function<MessageSupplier, MessageReply> action;
        private BiConsumer<Data, MessageReply> replyProcessor;

        private Function<Data, MessageSupplier>  reverseCommandSupplier;
        private Function<MessageSupplier, Boolean> reverseAction;

        private State resultState;

        public SagaStepBuilder(String stepName) {
            this.stepName = stepName;
        }

        public SagaStepBuilder<Data, State> action(Function<MessageSupplier, MessageReply> action, Function<Data, MessageSupplier> commandSupplier) {
            this.action = action;
            this.commandSupplier = commandSupplier;
            return this;
        }

        public <T extends MessageReply> SagaStepBuilder<Data, State> onReply(BiConsumer<Data, T> replyProcessor) {
            this.replyProcessor = (data, rawResponse) -> replyProcessor.accept(data, (T) rawResponse);
            return this;
        }

        public SagaStepBuilder<Data, State> compenstation(Function<MessageSupplier, Boolean> reverseAction, Function<Data, MessageSupplier>  reverseCommandSupplier) {
            this.reverseAction = reverseAction;
            this.reverseCommandSupplier = reverseCommandSupplier;
            return this;
        }

        public SagaStepBuilder<Data, State> resultState(State resultState) {
            this.resultState = resultState;
            return this;
        }

        public SagaStep<Data, State> build() {
            return new SagaStep(stepName, commandSupplier, action, replyProcessor, reverseCommandSupplier, reverseAction, resultState);
        }
    }

    private SagaStep(String stepName, Function<Data, MessageSupplier> commandSupplier,
                     Function<MessageSupplier, MessageReply> action,
                     BiConsumer<Data, MessageReply> replyProcessor,
                     Function<Data, MessageSupplier> reverseCommandSupplier,
                     Function<MessageSupplier, Boolean> reverseAction,
                     State resultState) {
        this.stepName = stepName;
        this.commandSupplier = commandSupplier;
        this.action = action;
        this.replyProcessor = replyProcessor;
        this.reverseCommandSupplier = reverseCommandSupplier;
        this.reverseAction = reverseAction;
        this.resultState = resultState;
    }

    public String getStepName() {
        return stepName;
    }

    public Function<Data, MessageSupplier> getCommandSupplier() {
        return commandSupplier;
    }

    public Function<MessageSupplier, MessageReply> getAction() {
        return action;
    }

    public BiConsumer<Data, MessageReply> getReplyProcessor() {
        return replyProcessor;
    }

    public Function<Data, MessageSupplier> getReverseCommandSupplier() {
        return reverseCommandSupplier;
    }

    public Function<MessageSupplier, Boolean> getReverseAction() {
        return reverseAction;
    }

    public State getResultState() {
        return resultState;
    }
}
