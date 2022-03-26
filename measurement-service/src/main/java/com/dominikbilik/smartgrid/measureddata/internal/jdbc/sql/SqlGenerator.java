package com.dominikbilik.smartgrid.measureddata.internal.jdbc.sql;

import com.dominikbilik.smartgrid.measureddata.internal.jdbc.TableDescription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

public interface SqlGenerator {

    /**
     * This method is used to select right SQL Generator
     * @param metadata The database metadata.
     */
    boolean isCompatible(DatabaseMetaData metadata) throws SQLException;


    String count(TableDescription table);

    String deleteAll(TableDescription table);

    String deleteById(TableDescription table);

    String deleteByIds(TableDescription table, int idsCount);

    String existsById(TableDescription table);

    String insert(TableDescription table, Map<String, Object> columns);

    String selectAll(TableDescription table);

    String selectAll(TableDescription table, Pageable page);

    String selectAll(TableDescription table, Sort sort);

    String selectById(TableDescription table);

    String selectByIds(TableDescription table, int idsCount);

    String update(TableDescription table, Map<String, Object> columns);

}
