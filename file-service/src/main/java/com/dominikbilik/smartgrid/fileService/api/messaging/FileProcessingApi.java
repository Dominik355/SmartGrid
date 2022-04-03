package com.dominikbilik.smartgrid.fileService.api.messaging;

import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileProcessingApi {

    @KafkaListener(id = "fileServiceGroup1", topics = "process_file", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("process_file_reply")
    public Message<ProcessFileCommandResponse> listen(ConsumerRecord<String, ProcessFileCommand> record) {
        System.out.println("Received: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());
        String k = record.key();

        ProcessFileCommandResponse response = new ProcessFileCommandResponse();
        response.setFileId(111111L);
        response.setFileName("testName");
        response.setDeviceIdFromFile("222222");
        response.setDeviceNameFromFile("testNameFromFile");
        System.out.println("Sending back : " + response);

        Message<ProcessFileCommandResponse> message = MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
        return message;
    }

}
