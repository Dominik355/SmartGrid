package com.dominikbilik.smartgrid.device.api.messaging;

import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommand;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommandResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class DeviceApi {

    @KafkaListener(id = "deviceServiceGroup1", topics = "verify_device", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("verify_device_reply")
    public Message<VerifyDeviceCommandResponse> listen(ConsumerRecord<String, VerifyDeviceCommand> record) {
        System.out.println("Received: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());

        VerifyDeviceCommandResponse response = new VerifyDeviceCommandResponse();
        response.setDeviceId(777777L);
        System.out.println("Sending back : " + response.getDeviceId());

        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
    }

}
