package com.dominikbilik.smartgrid.device.configuration.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.requestreply.CorrelationKey;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG;

@Configuration
public class KafkaConfiguration {

    protected static String BOOTSTRAP_SERVER;
    protected static Integer CLIENT_TIMEOUT;

    /*
        Use TYPE_MAPPINGS to map objects with different names , e.g.:"foo:test.common.Foo2,bar:test.common.Bar2"
     */
    protected static final String TYPE_MAPPINGS = "verifyDeviceCommand:com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommand," +
            "verifyDeviceCommandResponse:com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommandResponse";

    /*
        set correlation ID based on message key. Basic use of UUID is better in performance view, but i want to have readable
        parameters in my Kafka UI for better presentation of results, that's why i'm gonna use String as both key and correlationID.
     */
    public static final Function<ProducerRecord<String, Object>, CorrelationKey> CORRELATION_STRATEGY =
            rec -> new CorrelationKey(rec.key().getBytes(StandardCharsets.UTF_8));

    public static Map<String, Object> commonKafkaProperties() {
        return Collections.unmodifiableMap(new HashMap<>(){{
            put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
            put(REQUEST_TIMEOUT_MS_CONFIG, CLIENT_TIMEOUT);
        }});
    }

    @Value("${spring.kafka.bootstrap.server}")
    public void setBootstrapServer(String bootstrapServer) {
        BOOTSTRAP_SERVER = bootstrapServer;
    }

    @Value("${spring.kafka.client.timeout}")
    public void setClientTimeout(Integer clientTimeout) {
        CLIENT_TIMEOUT = clientTimeout;
    }

    @Bean
    public KafkaAdmin admin() {
        return new KafkaAdmin(commonKafkaProperties());
    }

}
