package com.dominikbilik.smartgrid.fileService.api.messaging;

import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ReverseProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileParserServiceImpl;
import com.dominikbilik.smartgrid.fileService.service.parser.impl.FileService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class FileProcessingApi {

    private static final Logger LOG = LoggerFactory.getLogger(FileProcessingApi.class);

    @Autowired
    private FileParserServiceImpl parserService;

    @Autowired
    private FileService fileService;

    /**
     * Processing file listener
     */
    @KafkaListener(id = "processFileGroup1", topics = "process_file", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("process_file_reply")
    public Message<ProcessFileCommandResponse> processFileListener(ConsumerRecord<String, ProcessFileCommand> record) {
        LOG.info("processFileListener: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());
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

        LOG.info("Files processed Sending back : " + response);
        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
    }

    /**
     * Unprocessing file listener
     */
    @KafkaListener(id = "unprocessFileGroup1", topics = "unprocess_file", containerFactory = "kafkaListenerContainerFactoryStringObject")
    public void unprocessFileListener(ConsumerRecord<String, ReverseProcessFileCommand> record) {
        LOG.info("unprocessFileListener: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());
        Assert.isTrue(record.value() != null && record.value().getFileId() != null, "FileId can not be null !!!");
        if (fileService.deleteFile(record.value().getFileId())) {
            LOG.info("File [fileId={}] deleted successfuly", record.value().getFileId());
        }
    }
}
