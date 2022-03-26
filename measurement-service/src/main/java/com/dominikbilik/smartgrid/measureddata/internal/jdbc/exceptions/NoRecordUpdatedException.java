package com.dominikbilik.smartgrid.measureddata.internal.jdbc.exceptions;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;

public class NoRecordUpdatedException extends IncorrectUpdateSemanticsDataAccessException {

    private final String tableName;
    private final Object[] id;


    public NoRecordUpdatedException(String tableName, Object... id) {
        super(format("No record with id = {%s} exists in table %s",
                arrayToCommaDelimitedString(id), tableName));
        this.tableName = tableName;
        this.id = id.clone();
    }

    public NoRecordUpdatedException(String tableName, String msg) {
        super(msg);
        this.tableName = tableName;
        this.id = new Object[0];
    }

    public NoRecordUpdatedException(String tableName, String msg, Throwable cause) {
        super(msg, cause);
        this.tableName = tableName;
        this.id = new Object[0];
    }


    @Override
    public boolean wasDataUpdated() {
        return false;
    }

    public String getTableName() {
        return tableName;
    }

    public Object[] getId() {
        return id.clone();
    }
}