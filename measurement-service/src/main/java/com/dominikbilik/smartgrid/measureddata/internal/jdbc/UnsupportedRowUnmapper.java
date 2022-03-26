package com.dominikbilik.smartgrid.measureddata.internal.jdbc;

import java.util.Map;

public class UnsupportedRowUnmapper<T> implements RowUnmapper<T> {

    public Map<String, Object> mapColumns(T o) {
        throw new UnsupportedOperationException(
                "This repository is read-only, it can't store or update entities");
    }

}