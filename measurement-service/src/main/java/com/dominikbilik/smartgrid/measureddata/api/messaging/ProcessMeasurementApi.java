package com.dominikbilik.smartgrid.measureddata.api.messaging;

import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommand;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommandResponse;
import com.dominikbilik.smartgrid.measureddata.service.ProcessingService;
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

@Component
public class ProcessMeasurementApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessMeasurementApi.class);

    @Autowired
    private ProcessingService processingService;

    @KafkaListener(id = "measurementServiceGroup1", topics = "process_measurement", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("process_measurement_reply")
    public Message<ProcessMeasurementCommandResponse> listen(ConsumerRecord<String, ProcessMeasurementCommand> record) {
        LOG.info("Received: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());


        ProcessMeasurementCommandResponse response;
        try {
            response = new ProcessMeasurementCommandResponse(
                    processingService.processMeasurement(record.value().getMeasurementFileId(), record.value().getDeviceId(), record.value().getMeasurementType()));
        } catch (Exception ex) {
            LOG.error("Exception occured while processing a measurement [FileId={}, deviceId={}]", record.value().getMeasurementFileId(), record.value().getDeviceId());
            ex.printStackTrace();
            return MessageBuilder.withPayload(new ProcessMeasurementCommandResponse())
                    .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                    .setHeader("exception_message", ex.getMessage())
                    .build();
        }

        LOG.info("[{}]Measurement processed. Sending back measurementId={}", record.key(), response.getMeasurementId());

        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
    }

}
