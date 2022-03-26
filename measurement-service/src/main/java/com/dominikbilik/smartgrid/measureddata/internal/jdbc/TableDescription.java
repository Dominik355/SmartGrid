package com.dominikbilik.smartgrid.measureddata.internal.jdbc;

import org.springframework.util.Assert;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

public class TableDescription {

    private String tableName;
    private String schemaName;
    private String selectClause = "*";
    private List<String> pkColumns = singletonList("id");
    private String tableSequencer;

    public TableDescription() {}

    public TableDescription(String tableName, String selectClause, List<String> pkColumns, String schemaName, String tableSequencer) {
        setTableName(tableName);
        setSelectClause(selectClause);
        setPkColumns(pkColumns);
    }

    public TableDescription(String tableName, String schemaName, String tableSequencer, String... pkColumns) {
        this(tableName, null, asList(pkColumns), schemaName, tableSequencer);
    }

    public TableDescription(String tableName, String... pkColumns) {
        this(tableName, null, asList(pkColumns), null, null);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        Assert.state(tableName != null, "tableName must not be null");
        return tableName;
    }

    public void setTableName(String tableName) {
        Assert.hasText(tableName, "tableName must not be blank");
        this.tableName = tableName;
    }

    public String getSelectClause() {
        return selectClause;
    }

    public void setSelectClause(String selectClause) {
        this.selectClause = selectClause != null ? selectClause : "*";
    }

    public String getFromClause() {
        return getFullTableName();
    }

    public List<String> getPkColumns() {
        return pkColumns;
    }

    public void setPkColumns(List<String> pkColumns) {
        if (pkColumns != null && !pkColumns.isEmpty()) {
            this.pkColumns = unmodifiableList(pkColumns);
        }
    }

    public void setPkColumns(String... idColumns) {
        setPkColumns(asList(idColumns));
    }

    public String getTableSequencer() {
        return tableSequencer;
    }

    public void setTableSequencer(String tableSequencer) {
        this.tableSequencer = tableSequencer;
    }

    public String getFullTableName() {
        StringBuilder fullTableName = new StringBuilder(getTableName());
        if (getSchemaName() != null && !getSchemaName().isBlank()) {
            fullTableName.insert(0, getSchemaName() + ".");
        }
        return fullTableName.toString();
    }
}
