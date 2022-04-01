package com.dominikbilik.smartgrid.datainput.configuration.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import static com.dominikbilik.smartgrid.datainput.configuration.kafka.TopicsConfiguration.DLT_SUFFIX;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
public class ConsumerKafkaConfiguration {

    private static final String RETRY_ATTEMPT_HEADER = "retry_attempt";

    private static final int RETRY_MAX_ATTEMPTS = 5;

    public static Map<String, Object> consumerConfigs() {
        Map<String, Object> properties= new HashMap<>();

        properties.put(ENABLE_AUTO_COMMIT_CONFIG, false); // we let spring to take care of commiting
        properties.put(AUTO_OFFSET_RESET_CONFIG, "latest");
        //properties.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, 1); // this way we commit immediatelly after receiving message (basic time is 5 seconds) - USE ONLY WITH AUTO COMMIT ENABLED

        properties.put(SESSION_TIMEOUT_MS_CONFIG, 15000);

        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, JsonDeserializer.class);

        properties.put(JsonDeserializer.TYPE_MAPPINGS, KafkaConfiguration.TYPE_MAPPINGS);
        properties.put("spring.json.trusted.packages", "*");

        properties.put(MAX_PARTITION_FETCH_BYTES_CONFIG, "20971520");
        properties.put(FETCH_MAX_BYTES_CONFIG, 50 * 1024 * 1024);

        properties.putAll(KafkaConfiguration.commonKafkaProperties());

        return properties;
    }

    @Bean("consumerFactoryStringObject")
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean("kafkaListenerContainerFactoryStringObject")
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setAutoStartup(true);
        factory.setBatchListener(false); // dont want to implement bulk message processing
        factory.setCommonErrorHandler(commonErrorHandler()); // handling errors by sending messages to Dead Letter Topic and logging exception
        return factory;
    }

    /*
            FixedBackOff -> zapinal sa , pokial som mal napriklad zle vytvoreneho producera v replytemplate -> dal som tma zly serializer
            Vtedy akoby presimulovalo cely proces znova dany pocet krat v danych odstupoch. Takze zobralo spravu ,ktora sa nepodarila
            poslat , injectla do kafkaListener-u a akokeby prebehlo cele prijatie spravy znova. S tymto sa zapodievat nechcem,
            takze to neham na nule. Zostanem pri tom, ze pokial pri prijati nastane chyba, tak sa zoberie sprava, nastavi sa jej retry header
            a posle sa nanovo na topic ako nova sprava, takto sa nahradi COMPACT-om existujuca sprava s rovnakym klucom a iba sa posunie offset
     */
    @Bean("dltErrorHandler")
    public CommonErrorHandler commonErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(dltRecoverer(), new FixedBackOff(0, 0)); // popis vyssie
        errorHandler.setCommitRecovered(true);
        return errorHandler;
    }

    @Bean
    public DeadLetterPublishingRecoverer dltRecoverer() {
        return new DeadLetterPublishingRecoverer(
                createDltKafkaTemplate(),
                (record, exception) -> new TopicPartition(record.topic() + DLT_SUFFIX, record.partition()));
    }

    /**
     * to make sure, we won't get any serialization Exception, we use Object as a Key, so we can calmly
     * serialize all: Long, Json and String (not sure with byte[]).
     */
    private KafkaTemplate<Object, Object> createDltKafkaTemplate() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.putAll(KafkaConfiguration.commonKafkaProperties());
        KafkaTemplate<Object, Object> template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(properties));
        return template;
    }

}
