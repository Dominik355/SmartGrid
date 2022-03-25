package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.Record;
import com.dominikbilik.smartgrid.measureddata.internal.jdbc.RowUnmapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RecordRepository {

    public static final RowMapper<Record> ROW_MAPPER =
            (ResultSet rs, int rowNum) -> new Record(
                rs.getDouble("value"),
                rs.getTimestamp("date_time_from").toLocalDateTime(),
                rs.getTimestamp("date_time_to").toLocalDateTime(),
                rs.getLong("dataset_id"),
                rs.getLong("quantity_details_id")
        );

    //public static final RowUnmapper<Record> ROW_UNMAPPER =

}
