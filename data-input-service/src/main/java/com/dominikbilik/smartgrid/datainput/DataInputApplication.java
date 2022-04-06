package com.dominikbilik.smartgrid.datainput;

import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.NewMeasurementHandler;
import com.dominikbilik.smartgrid.datainput.saga.sagas.newMeasurement.NewMeasurementSaga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class DataInputApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataInputApplication.class, args);
    }

    @Autowired
    private NewMeasurementHandler handler;
/*
    @Bean
    public CommandLineRunner runner() {
        return args -> {
            NewMeasurementSaga saga = new NewMeasurementSaga(handler);
            saga.start();
        };
    }
*/
}
