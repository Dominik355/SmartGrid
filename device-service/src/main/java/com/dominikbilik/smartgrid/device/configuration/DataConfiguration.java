package com.dominikbilik.smartgrid.device.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.dominikbilik.smartgrid.device")
public class DataConfiguration {
}
