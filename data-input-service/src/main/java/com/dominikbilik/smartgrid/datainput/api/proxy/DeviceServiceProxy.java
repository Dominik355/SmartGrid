package com.dominikbilik.smartgrid.datainput.api.proxy;

import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommand;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static com.dominikbilik.smartgrid.datainput.configuration.kafka.KafkaConfiguration.CORRELATION_STRATEGY;

@Component
public class DeviceServiceProxy extends Proxy {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceServiceProxy.class);

    public static final long RESPONSE_TIMEOUT_SECONDS = 5;
    private static final String VERIFY_DEVICE_TOPIC = "verify_device";
    private static final String VERIFY_DEVICE_TOPIC_REPLY = "verify_device_reply";
    private static final String CONSUMER_GROUP_ID = "inputService_device_group1";

    private ReplyingKafkaTemplate<String, Object, Object> template;


    @Autowired
    protected DeviceServiceProxy(ProducerFactory<String, Object> producerFactory,
                                 ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        super(producerFactory, containerFactory);
    }

    @PostConstruct
    private void init() {
        ConcurrentMessageListenerContainer<String, Object> container = createContainer(VERIFY_DEVICE_TOPIC_REPLY);
        container.getContainerProperties().setGroupId(CONSUMER_GROUP_ID);

        template = new ReplyingKafkaTemplate<>(getProducerFactory(), container);
        template.setCorrelationIdStrategy(CORRELATION_STRATEGY);
        template.start();
    }

    public RequestReplyFuture<String, Object, Object> verifyDevice(MessageSupplier<VerifyDeviceCommand> command) {
        Assert.notNull(command, "Command can not be null");
        Assert.notNull(command.getMessage(), "message can not be null");
        Assert.notNull(command.getKey(), "Key can not be null");
        LOG.info("verifyDevice: Sending VerifyDeviceCommand to a topic {} with a key {}. Timeout set to {} seconds", VERIFY_DEVICE_TOPIC, command.getKey(), RESPONSE_TIMEOUT_SECONDS);
        if (command.getMessage().getTopic() != null && !VERIFY_DEVICE_TOPIC.equals(command.getMessage().getTopic())) {
            throw new RuntimeException("Topic name of the message [" + command.getMessage().getTopic() + "] does not match topic name of this Producer [" + VERIFY_DEVICE_TOPIC + "]");
        }
        return template.sendAndReceive(
                new ProducerRecord<>(VERIFY_DEVICE_TOPIC, command.getKey(), command.getMessage()),
                Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS)
        );
    }
}
