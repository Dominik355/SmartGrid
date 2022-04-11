package com.dominikbilik.smartgrid.measureddata.domain.repository;

import com.dominikbilik.smartgrid.measureddata.domain.entity.Record;
import com.dominikbilik.smartgrid.measureddata.internal.jdbc.RowUnmapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Repository
public class RecordRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String RECORDS_SEARCH_SQL = "select * from records where date_time_from >= ? and date_time_to <= ? and dataset_id = ? and quantity_details_id = ?";

    private static final RowMapper<Record> ROW_MAPPER =
            (ResultSet rs, int rowNum) -> new Record(
                rs.getDouble("value"),
                rs.getTimestamp("date_time_from").toLocalDateTime(),
                rs.getTimestamp("date_time_to").toLocalDateTime(),
                rs.getLong("dataset_id"),
                rs.getLong("quantity_details_id")
        );

    public int save(Record record) {
        return jdbcTemplate.update("INSERT INTO records VALUES (?, ?, ?, ?, ?)",
                record.getValue(),
                record.getDateTimeFrom(),
                record.getDateTimeTo(),
                record.getDatasetId(),
                record.getQuantityDetailsId());
    }

    public int[] batchInsert(List<Record> records) {
        return this.jdbcTemplate.batchUpdate(
                "INSERT INTO records VALUES (?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setDouble(1, records.get(i).getValue());
                        ps.setTimestamp(2, Timestamp.valueOf(records.get(i).getDateTimeFrom()));
                        ps.setTimestamp(3, Timestamp.valueOf(records.get(i).getDateTimeTo()));
                        ps.setLong(4, records.get(i).getDatasetId());
                        ps.setLong(5, records.get(i).getQuantityDetailsId());

                    }
                    public int getBatchSize() {
                        return records.size();
                    }
                });
    }

    public List<Record> getRecords(LocalDateTime from, LocalDateTime to, Long datasetId, Long quantityDetailId) {
        return jdbcTemplate.query(RECORDS_SEARCH_SQL, ROW_MAPPER,
                Timestamp.valueOf(from),
                Timestamp.valueOf(to),
                datasetId,
                quantityDetailId);
    }

}
