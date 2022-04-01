package com.dominikbilik.smartgrid.datainput.saga.impl;

import com.dominikbilik.smartgrid.datainput.saga.participants.fileService.*;
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
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static com.dominikbilik.smartgrid.datainput.configuration.kafka.KafkaConfiguration.CORRELATION_STRATEGY;

@Component
public class FileServiceProxy extends Proxy<ProcessFileCommand, ReverseProcessFileCommand> {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceProxy.class);

    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(15); // set longer ,if file processing would take more time
    private static final String PROCESS_FILE_TOPIC = "process_file_command";
    private static final String PROCESS_FILE_TOPIC_REPLY = "process_file_command_reply";
    private static final String CONSUMER_GROUP_ID = "inputService_file_group1";

    private ReplyingKafkaTemplate<String, Object, Object> template;

    @Autowired
    public FileServiceProxy(ProducerFactory<String, Object> producerFactory,
                            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        super(producerFactory, containerFactory);
    }

    @PostConstruct
    private void init() {
        ConcurrentMessageListenerContainer<String, Object> container = createContainer(PROCESS_FILE_TOPIC_REPLY);
        container.getContainerProperties().setGroupId(CONSUMER_GROUP_ID);

        template = new ReplyingKafkaTemplate<>(getProducerFactory(), container);
        template.setCorrelationIdStrategy(CORRELATION_STRATEGY);
        template.start();
    }

    @Override
    public RequestReplyFuture<String, Object, Object> executeCommand(ProcessFileCommand command, String key) {
        LOG.debug("Sending ProcessFileCommand to a topic {} with a key {}. Timeout set to {}", PROCESS_FILE_TOPIC, key, RESPONSE_TIMEOUT);
        return template.sendAndReceive(
                new ProducerRecord<>(PROCESS_FILE_TOPIC, 1, key, command),
                RESPONSE_TIMEOUT
        );
    }

    @Override
    public ListenableFuture<SendResult<String, Object>> executeReverseCommand(ReverseProcessFileCommand command, String key) {
        LOG.debug("Sending ReverseProcessFileCommand to a topic {} with a key {}", PROCESS_FILE_TOPIC_REPLY, key);
        return template.send(
                new ProducerRecord<>(PROCESS_FILE_TOPIC, 1, key, command)
        );
    }
}
