package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.SequenceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PrimaryKeyInjector {

    private static final Logger LOG = LoggerFactory.getLogger(PrimaryKeyInjector.class);

    private static final String SEQUENCE_SUFFIX = "_seq";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Inject ID from sequencer before inserting entity. Works for entities implementig ParentEntity
     */
    @Bean
    ApplicationListener<BeforeSaveEvent<Object>> loggingSaves() {
        return event -> {
            LOG.info("event entity type: " + event.getAggregateChange().getEntityType());
            if (!(event.getEntity() instanceof SequenceID)) {
                LOG.info("entity {} is not implementing ParentEntity, ID wont be filled in BeforeSaveEvent", event.getEntity());
                return;
            }

            SequenceID entity = (SequenceID) event.getEntity();
            if (entity.getId() != null) {
                return;
            }

            try {
                entity.setId(jdbcTemplate.queryForObject("SELECT nextval('" + entity.getTablename() + SEQUENCE_SUFFIX + "')", Long.class));
            } catch(Exception ex) {
                LOG.info("Error occured while trying to obtain value from sequence. Check if sequence {} exists", entity.getTablename() + SEQUENCE_SUFFIX);
                throw ex;
            }

            LOG.info("{} is getting saved. ID set to {}", event.getEntity(), entity.getId());
        };
    }

}
