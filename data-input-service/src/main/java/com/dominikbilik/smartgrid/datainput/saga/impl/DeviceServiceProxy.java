package com.dominikbilik.smartgrid.datainput.saga.impl;

import com.dominikbilik.smartgrid.datainput.saga.participants.deviceService.VerifyDeviceCommand;
import com.dominikbilik.smartgrid.datainput.saga.participants.deviceService.VerifyDeviceCommandReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class DeviceServiceProxy extends Proxy<VerifyDeviceCommand, Object> {


    @Override
    public RequestReplyFuture<String, Object, Object> executeCommand(VerifyDeviceCommand command, String key) {
        return null;
    }

    @Override
    public ListenableFuture<SendResult<String, Object>> executeReverseCommand(Object command, String key) {
        return null;
    }
}
