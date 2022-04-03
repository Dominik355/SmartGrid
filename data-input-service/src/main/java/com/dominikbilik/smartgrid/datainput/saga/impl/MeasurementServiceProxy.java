package com.dominikbilik.smartgrid.datainput.saga.impl;

import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommand;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ReverseProcessMeasurementCommand;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static com.dominikbilik.smartgrid.datainput.configuration.kafka.KafkaConfiguration.CORRELATION_STRATEGY;

@Component
public class MeasurementServiceProxy extends Proxy {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementServiceProxy.class);

    public static final long RESPONSE_TIMEOUT_SECONDS = 20; // kinda longer ,if whole measurement processing would take unexpectedly more time
    private static final String PROCESS_MEASUREMENT_TOPIC = "process_measurement";
    private static final String UNPROCESS_MEASUREMENT_TOPIC = "unprocess_measurement";
    private static final String PROCESS_MEASUREMENT_TOPIC_REPLY = "process_measurement_reply";
    private static final String CONSUMER_GROUP_ID = "inputService_measurement_group1";

    private ReplyingKafkaTemplate<String, Object, Object> template;

    @Autowired
    public MeasurementServiceProxy(ProducerFactory<String, Object> producerFactory,
                            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        super(producerFactory, containerFactory);
    }

    @PostConstruct
    private void init() {
        ConcurrentMessageListenerContainer<String, Object> container = createContainer(PROCESS_MEASUREMENT_TOPIC_REPLY);
        container.getContainerProperties().setGroupId(CONSUMER_GROUP_ID);

        template = new ReplyingKafkaTemplate<>(getProducerFactory(), container);
        template.setCorrelationIdStrategy(CORRELATION_STRATEGY);
        template.start();
    }

    public RequestReplyFuture<String, Object, Object> processMeasurement(MessageSupplier<ProcessMeasurementCommand> command) {
        Assert.notNull(command, "Command can not be null");
        Assert.notNull(command.getMessage(), "message can not be null");
        Assert.notNull(command.getKey(), "Key can not be null");
        LOG.debug("processMeasurement: Sending ProcessMeasurementCommand to a topic {} with a key {}. Timeout set to {}", PROCESS_MEASUREMENT_TOPIC, command.getKey(), RESPONSE_TIMEOUT_SECONDS);
        if (command.getMessage().getTopic() != null && !PROCESS_MEASUREMENT_TOPIC.equals(command.getMessage().getTopic())) {
            throw new RuntimeException("Topic name of the message [" + command.getMessage().getTopic() + "] does not match topic name of this Producer [" + PROCESS_MEASUREMENT_TOPIC + "]");
        }

        return template.sendAndReceive(
                new ProducerRecord<>(PROCESS_MEASUREMENT_TOPIC, command.getKey(), command),
                Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS)
        );
    }

    /*
    public ListenableFuture<SendResult<String, Object>> reverseProcessMeasurement(ReverseProcessMeasurementCommand command, String key) {
        LOG.debug("reverseProcessMeasurement: Sending ReverseProcessFileCommand to a topic {} with a key {}", UNPROCESS_MEASUREMENT_TOPIC, key);
        return template.send(
                new ProducerRecord<>(UNPROCESS_MEASUREMENT_TOPIC, 1, key, command)
        );
    }
     */

}
