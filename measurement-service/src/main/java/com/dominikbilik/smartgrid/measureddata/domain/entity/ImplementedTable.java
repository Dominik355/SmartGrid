package com.dominikbilik.smartgrid.measureddata.domain.entity;

import java.time.LocalDateTime;

public class ImplementedTable {
    private long id;
    private int version;
    private String tableName;
    private String className;
    private LocalDateTime creationDate;
    private long datasetId;
}
