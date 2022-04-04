package com.dominikbilik.smartgrid.datainput.api.proxy;

import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ReverseProcessFileCommand;
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
public class FileServiceProxy extends Proxy {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceProxy.class);

    public static final long RESPONSE_TIMEOUT_SECONDS = 15L; // set longer ,if file processing would take more time
    private static final String PROCESS_FILE_TOPIC = "process_file";
    private static final String UNPROCESS_FILE_TOPIC = "unprocess_file";
    private static final String PROCESS_FILE_TOPIC_REPLY = "process_file_reply";
    private static final String CONSUMER_GROUP_ID = "inputService_file_group1";

    private ReplyingKafkaTemplate<String, Object, Object> template;

    @Autowired
    public FileServiceProxy(ProducerFactory<String, Object> producerFactory,
                            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        super(producerFactory, containerFactory);
        System.out.println("key serializer ce: " + producerFactory.getKeySerializer());
        System.out.println("value serializer ce: " + producerFactory.getConfigurationProperties());
    }

    @PostConstruct
    private void init() {
        ConcurrentMessageListenerContainer<String, Object> container = createContainer(PROCESS_FILE_TOPIC_REPLY);
        container.getContainerProperties().setGroupId(CONSUMER_GROUP_ID);

        template = new ReplyingKafkaTemplate<>(getProducerFactory(), container);
        System.out.println("key serializer: " + template.getProducerFactory().getKeySerializer());
        System.out.println("value serializer: " + template.getProducerFactory().getValueSerializer());
        template.setCorrelationIdStrategy(CORRELATION_STRATEGY);
        template.start();
    }

    public RequestReplyFuture<String, Object, Object> processFile(MessageSupplier<ProcessFileCommand> command) {
        Assert.notNull(command, "Command can not be null");
        Assert.notNull(command.getMessage(), "message can not be null");
        Assert.notNull(command.getKey(), "Key can not be null");
        LOG.info("processFile: Sending ProcessFileCommand to a topic {} with a key {} for fileName {}. Timeout set to {} seconds", PROCESS_FILE_TOPIC, command.getKey(), command.getMessage().getFileName(), RESPONSE_TIMEOUT_SECONDS);
        if (command.getMessage().getTopic() != null && !PROCESS_FILE_TOPIC.equals(command.getMessage().getTopic())) {
            throw new RuntimeException("Topic name of the message [" + command.getMessage().getTopic() + "] does not match topic name of this Producer [" + PROCESS_FILE_TOPIC + "]");
        }
        return template.sendAndReceive(
                new ProducerRecord<>(PROCESS_FILE_TOPIC, command.getKey(), command.getMessage()),
                Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS)
        );
    }

    public ListenableFuture<SendResult<String, Object>> unProcessFile(MessageSupplier<ReverseProcessFileCommand> command) {
        Assert.notNull(command, "Command can not be null");
        Assert.notNull(command.getMessage(), "message can not be null");
        Assert.notNull(command.getKey(), "Key can not be null");
        LOG.info("reverseProcessingFile: Sending ReverseProcessFileCommand to a topic {} with a key {} for fileId {}", UNPROCESS_FILE_TOPIC, command.getKey(), command.getMessage().getFileId());
        if (command.getMessage().getTopic() != null && !UNPROCESS_FILE_TOPIC.equals(command.getMessage().getTopic())) {
            throw new RuntimeException("Topic name of the message [" + command.getMessage().getTopic() + "] does not match topic name of this Producer [" + UNPROCESS_FILE_TOPIC + "]");
        }
        return template.send(
                new ProducerRecord<>(UNPROCESS_FILE_TOPIC, command.getKey(), command.getMessage())
        );
    }

}
