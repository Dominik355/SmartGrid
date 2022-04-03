package com.dominikbilik.smartgrid.datainput.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.common.config.TopicConfig.*;

@Configuration
@DependsOn("kafkaConfiguration")
public class TopicsConfiguration {

    public static final String DLT_SUFFIX = ".DLT";

    private static final Map<String, String> commonProperties = Collections.unmodifiableMap(new HashMap<>(){{
            put(MAX_MESSAGE_BYTES_CONFIG, String.valueOf(1024 * 1024 * 10));
            put(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT + "," + CLEANUP_POLICY_DELETE);
            put(RETENTION_BYTES_CONFIG, String.valueOf(1024 * 1024 * 50)); // max partition size is 50MB
            put(RETENTION_MS_CONFIG, String.valueOf(1000 * 60 * 60 * 24 * 3)); // This configuration controls the maximum time we will retain a log before we will discard old log segments to free up space if we are using the "delete" retention policy. This represents an SLA on how soon consumers must read their data. If set to -1, no time limit is applied.
    }});

    @Bean
    public NewTopic processFileTopic() {
        return new NewTopic("process_file", 1, (short) 1).configs(commonProperties);
    }

    @Bean
    public NewTopic processFileTopicDLT() {
        return new NewTopic("process_file" + DLT_SUFFIX, 1, (short) 1).configs(commonProperties);
    }

    @Bean
    public NewTopic processFileReplyTopic() {
        return new NewTopic("process_file_reply", 1, (short) 1).configs(commonProperties);
    }

    @Bean
    public NewTopic processFileReplyTopicDLT() {
        return new NewTopic("process_file_reply" + DLT_SUFFIX, 1, (short) 1).configs(commonProperties);
    }

}
