package com.dominikbilik.smartgrid.datainput.api.proxy;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

public abstract class Proxy {

    private ProducerFactory<String, Object> producerFactory;
    private ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory;

    protected Proxy(ProducerFactory<String, Object> producerFactory,
                    ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        this.producerFactory = producerFactory;
        this.containerFactory = containerFactory;
    }

    protected ProducerFactory<String, Object> getProducerFactory() {
        return producerFactory;
    }

    protected ConcurrentMessageListenerContainer<String, Object> createContainer(String topic) {
        return this.containerFactory.createContainer(topic);
    }

}
