package com.dominikbilik.smartgrid.measureddata.internal.jdbc.sql;

import com.dominikbilik.smartgrid.measureddata.internal.jdbc.TableDescription;
import org.springframework.data.domain.Pageable;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class LimitOffsetSqlGenerator extends DefaultSqlGenerator {

    private static final List<String> SUPPORTED_PRODUCTS =
            asList("PostgreSQL", "H2", "HSQL Database Engine", "MySQL");


    @Override
    public boolean isCompatible(DatabaseMetaData metadata) throws SQLException {
        return SUPPORTED_PRODUCTS.contains(metadata.getDatabaseProductName());
    }

    @Override
    public String selectAll(TableDescription table, Pageable page) {
        return format("%s LIMIT %d OFFSET %d",
                selectAll(table, page.getSort()), page.getPageSize(), page.getOffset());
    }
}