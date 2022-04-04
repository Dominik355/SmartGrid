package com.dominikbilik.smartgrid.datainput.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.dominikbilik.smartgrid.datainput")
public class DatabaseConfiguration {
}
