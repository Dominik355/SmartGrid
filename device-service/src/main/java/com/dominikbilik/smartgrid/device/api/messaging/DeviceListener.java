package com.dominikbilik.smartgrid.device.api.messaging;

import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommand;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommandResponse;
import com.dominikbilik.smartgrid.device.service.DeviceService;
import org.apache.commons.lang.StringUtils;
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
public class DeviceListener {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceListener.class);

    @Autowired
    private DeviceService deviceService;

    @KafkaListener(id = "deviceServiceGroup1", topics = "verify_device", containerFactory = "kafkaListenerContainerFactoryStringObject")
    @SendTo("verify_device_reply")
    public Message<VerifyDeviceCommandResponse> listen(ConsumerRecord<String, VerifyDeviceCommand> record) {
        LOG.info("Received: " + record.value() + ", with key: " + record.key() + ", with offset: " + record.offset() + ", with Headers: " + record.headers());
        Assert.notNull(StringUtils.isNotBlank(record.value().getFileDeviceId()) || StringUtils.isNotBlank(record.value().getDeviceName()), "either ID or name can not be null");

        return MessageBuilder
                .withPayload(new VerifyDeviceCommandResponse(
                        deviceService.verifyDevice(record.value().getFileDeviceId(), record.value().getDeviceName())))
                .setHeader(KafkaHeaders.MESSAGE_KEY, record.key())
                .build();
    }

}
