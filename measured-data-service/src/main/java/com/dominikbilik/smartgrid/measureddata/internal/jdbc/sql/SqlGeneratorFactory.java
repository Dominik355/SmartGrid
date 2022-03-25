package com.dominikbilik.smartgrid.measureddata.internal.jdbc.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SqlGeneratorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SqlGeneratorFactory.class);

    private static final SqlGeneratorFactory INSTANCE = new SqlGeneratorFactory(true);

    private final Set<SqlGenerator> generators = new HashSet<>();

    public SqlGeneratorFactory(boolean registerDefault) {
        if (registerDefault) {
            registerGenerator(new DefaultSqlGenerator());
            registerGenerator(new LimitOffsetSqlGenerator());
        }
    }

    /**
     * @return The singleton instance
     */
    public static SqlGeneratorFactory getInstance() {
        return INSTANCE;
    }

    public SqlGenerator getGenerator(DataSource dataSource) {
        DatabaseMetaData metaData;
        try {
            metaData = dataSource.getConnection().getMetaData();
        } catch (SQLException ex) {
            throw new DataAccessResourceFailureException("Failed to retrieve database metadata", ex);
        }

        for (SqlGenerator generator : generators) {
            try {
                if (generator.isCompatible(metaData)) {
                    LOG.info("Using SQL Generator {} for dataSource {}",
                            generator.getClass().getName(), dataSource.getClass());
                    return generator;
                }
            } catch (SQLException ex) {
                LOG.warn("Exception occurred when invoking isCompatible() on {}",
                        generator.getClass().getSimpleName(), ex);
            }
        }

        throw new IllegalStateException("No compatible SQL Generator found.");
    }

    public void registerGenerator(SqlGenerator sqlGenerator) {
        generators.add(sqlGenerator);
    }

}
