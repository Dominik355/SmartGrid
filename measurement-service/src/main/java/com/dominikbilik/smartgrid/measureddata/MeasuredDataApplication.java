package com.dominikbilik.smartgrid.measureddata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MeasuredDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeasuredDataApplication.class, args);
    }

}
