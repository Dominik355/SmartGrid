package com.dominikbilik.smartgrid.measureddata.api.messaging;

import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommand;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommandResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class ProcessMeasurementApi {

    @KafkaListener(id = "measurementServiceGroup1", topics = "process_measurement", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("process_measurement_reply")
    public Message<ProcessMeasurementCommandResponse> listen(ConsumerRecord<String, ProcessMeasurementCommand> record) {
        System.out.println("Received: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());

        ProcessMeasurementCommandResponse response = new ProcessMeasurementCommandResponse();
        response.setMeasurementId(123454321L);
        System.out.println("Sending back : " + response.getMeasurementId());

        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
    }

}
