package com.dominikbilik.smartgrid.fileService.api.messaging;

import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileParserServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileProcessingApi {

    @Autowired
    private FileParserServiceImpl parserService;

    @KafkaListener(id = "fileServiceGroup1", topics = "process_file", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("process_file_reply")
    public Message<ProcessFileCommandResponse> listen(ConsumerRecord<String, ProcessFileCommand> record) {
        System.out.println("Received: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());
        ProcessFileCommandResponse response;
        try {
            response = parserService.parseSavedFile(record.value().getFileName());
        } catch (Exception ex) {
            ex.printStackTrace();
            return MessageBuilder.withPayload(new ProcessFileCommandResponse())
                    .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                    .setHeader("exception_message", ex.getMessage())
                    .build();
        }
        System.out.println("Sending back : " + response);

        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
    }

}
