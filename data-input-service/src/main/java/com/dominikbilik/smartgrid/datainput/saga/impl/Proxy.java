package com.dominikbilik.smartgrid.datainput.saga.impl;

import com.dominikbilik.smartgrid.datainput.saga.participants.Command;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

/**
 *
 * @param <C> - Command type
 * @param <R> - Reverse command type
 */
public abstract class Proxy<C extends Command, R extends Command> {

    private ProducerFactory<String, Object> producerFactory;
    private ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory;

    protected Proxy(ProducerFactory<String, Object> producerFactory,
                    ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        this.producerFactory = producerFactory;
        this.containerFactory = containerFactory;
    }

    public abstract RequestReplyFuture<String, Object, Object> executeCommand(C command, String key);

    public abstract ListenableFuture<SendResult<String, Object>> executeReverseCommand(R command, String key);

    protected ProducerFactory<String, Object> getProducerFactory() {
        return producerFactory;
    }

    protected ConcurrentMessageListenerContainer<String, Object> createContainer(String topic) {
        return this.containerFactory.createContainer(topic);
    }
}
