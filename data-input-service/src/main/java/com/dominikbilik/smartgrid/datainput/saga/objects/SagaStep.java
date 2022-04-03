package com.dominikbilik.smartgrid.datainput.saga.objects;

import com.dominikbilik.smartgrid.common.model.MessageReply;
import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.SagaState;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SagaStep<Reply extends MessageReply, Data, State extends SagaState<State>> {

    // creates MessageSupplier with message and kafka message key
    private final Function<Data, MessageSupplier> commandSupplier;
    // sends command and returns reply
    private final Function<MessageSupplier, Reply> action;
    // process reply and updates saga state and data
    private final BiConsumer<Data, Reply> replyProcessor;

    // creates MessageSupplier for reverse command
    private final Function<Data, MessageSupplier>  reverseCommandSupplier;
    // evaluate whether the message was sent successfully
    private final Function<MessageSupplier, Boolean> reverseAction;

    private final State resultState;

    public static class SagaStepBuilder<Reply extends MessageReply, Data, State extends SagaState<State>> {
        private Function<Data, MessageSupplier> commandSupplier;
        private Function<MessageSupplier, Reply> action;
        private BiConsumer<Data, Reply> replyProcessor;

        private Function<Data, MessageSupplier>  reverseCommandSupplier;
        private Function<MessageSupplier, Boolean> reverseAction;

        private State resultState;

        public SagaStepBuilder<Reply, Data, State> action(Function<MessageSupplier, Reply> action, Function<Data, MessageSupplier> commandSupplier) {
            this.action = action;
            this.commandSupplier = commandSupplier;
            return this;
        }

        public SagaStepBuilder<Reply, Data, State> onReply(BiConsumer<Data, Reply> replyProcessor) {
            this.replyProcessor = replyProcessor;
            return this;
        }

        public SagaStepBuilder<Reply, Data, State> compenstation(Function<MessageSupplier, Boolean> reverseAction, Function<Data, MessageSupplier>  reverseCommandSupplier) {
            this.reverseAction = reverseAction;
            this.reverseCommandSupplier = reverseCommandSupplier;
            return this;
        }

        public SagaStepBuilder<Reply, Data, State> resultState(State resultState) {
            this.resultState = resultState;
            return this;
        }

        public SagaStep<Reply, Data, State> build() {
            return new SagaStep(commandSupplier, action, replyProcessor, reverseCommandSupplier, reverseAction, resultState);
        }
    }

    private SagaStep(Function<Data, MessageSupplier> commandSupplier,
                     Function<MessageSupplier, Reply> action,
                     BiConsumer<Data, Reply> replyProcessor,
                     Function<Data, MessageSupplier> reverseCommandSupplier,
                     Function<MessageSupplier, Boolean> reverseAction,
                     State resultState) {
        this.commandSupplier = commandSupplier;
        this.action = action;
        this.replyProcessor = replyProcessor;
        this.reverseCommandSupplier = reverseCommandSupplier;
        this.reverseAction = reverseAction;
        this.resultState = resultState;
    }

    public Function<Data, MessageSupplier> getCommandSupplier() {
        return commandSupplier;
    }

    public Function<MessageSupplier, Reply> getAction() {
        return action;
    }

    public BiConsumer<Data, Reply> getReplyProcessor() {
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
