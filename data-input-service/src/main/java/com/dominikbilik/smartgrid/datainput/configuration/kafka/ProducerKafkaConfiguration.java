package com.dominikbilik.smartgrid.datainput.configuration.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@DependsOn({"kafkaConfiguration","topicsConfiguration"})
public class ProducerKafkaConfiguration {

    public Map<String, Object> producerConfigs() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(MAX_REQUEST_SIZE_CONFIG, 1024 * 1024 * 10); // 10MB max size (basic is 1M)

        properties.put(JsonSerializer.TYPE_MAPPINGS, KafkaConfiguration.TYPE_MAPPINGS);

        properties.putAll(KafkaConfiguration.commonKafkaProperties());

        return properties;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(producerConfigs());
        System.out.println("rovno v configu : " + factory.getKeySerializer());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        template.setMicrometerEnabled(true);
        return template;
    }

}
