package com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement;

import com.dominikbilik.smartgrid.datainput.saga.MessageSupplier;
import com.dominikbilik.smartgrid.datainput.saga.impl.DeviceServiceProxy;
import com.dominikbilik.smartgrid.datainput.saga.impl.FileServiceProxy;
import com.dominikbilik.smartgrid.datainput.saga.impl.MeasurementServiceProxy;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommand;
import com.dominikbilik.smartgrid.device.api.v1.events.VerifyDeviceCommandResponse;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommand;
import com.dominikbilik.smartgrid.fileService.api.v1.events.ProcessFileCommandResponse;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommand;
import com.dominikbilik.smartgrid.measureddata.api.v1.events.ProcessMeasurementCommandResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.dominikbilik.smartgrid.datainput.saga.SagaCommons.EXCEPTION_MESSAGE_HEADER;

@Component
public class NewMeasurementHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NewMeasurementHandler.class);

    private FileServiceProxy fileServiceProxy;
    private DeviceServiceProxy deviceServiceProxy;
    private MeasurementServiceProxy measurementServiceProxy;

    private ObjectMapper objectMapper;

    @Autowired
    public NewMeasurementHandler(FileServiceProxy fileServiceProxy,
                                 DeviceServiceProxy deviceServiceProxy,
                                 MeasurementServiceProxy measurementServiceProxy,
                                 ObjectMapper objectMapper) {
        this.fileServiceProxy = fileServiceProxy;
        this.deviceServiceProxy = deviceServiceProxy;
        this.measurementServiceProxy = measurementServiceProxy;
        this.objectMapper = objectMapper;
    }

    public ProcessFileCommandResponse processFile(MessageSupplier<ProcessFileCommand> supplier) {
        ProcessFileCommandResponse response;
        try {
            RequestReplyFuture<String, Object, Object> future = fileServiceProxy.processFile(supplier);
            ConsumerRecord<String, Object> record = future.get(15, TimeUnit.SECONDS);
            checkException(record);
            response = objectMapper.convertValue(record.value(), ProcessFileCommandResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Problem occured while processing file", ex);
        }
        return response;
    }

    public Boolean unprocessFile(MessageSupplier supplier) {
        Object obj;
        try {
            ListenableFuture<SendResult<String, Object>> future = fileServiceProxy.unProcessFile(supplier);
            obj = future.get(FileServiceProxy.RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Problem occured while processing file", ex);
        }
        return (obj != null);
    }

    public VerifyDeviceCommandResponse verifyDevice(MessageSupplier<VerifyDeviceCommand> supplier) {
        VerifyDeviceCommandResponse response;
        try {
            RequestReplyFuture<String, Object, Object> future = deviceServiceProxy.verifyDevice(supplier);
            ConsumerRecord<String, Object> record = future.get(DeviceServiceProxy.RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            checkException(record);
            response = objectMapper.convertValue(record.value(), VerifyDeviceCommandResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Problem occured while verifying device", ex);
        }
        return response;
    }

    public ProcessMeasurementCommandResponse processMeasurement(MessageSupplier<ProcessMeasurementCommand> supplier) {
        ProcessMeasurementCommandResponse response;
        try {
            RequestReplyFuture<String, Object, Object> future = measurementServiceProxy.processMeasurement(supplier);
            ConsumerRecord<String, Object> record = future.get(MeasurementServiceProxy.RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            checkException(record);
            response = objectMapper.convertValue(record.value(), ProcessMeasurementCommandResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Problem occured while processing measurement", ex);
        }
        return response;
    }

    private void checkException(ConsumerRecord<String, Object> record) {
        Header header = record.headers().lastHeader(EXCEPTION_MESSAGE_HEADER);
        if (header != null) {
            throw new RuntimeException(
                    String.format("Exception found for ConsumerRecord [key = %s, topic = %s] with Exception Message= %s",
                            record.key(),
                            record.topic(),
                            new String(header.value(), StandardCharsets.UTF_8))
            );
        }
    }

}
