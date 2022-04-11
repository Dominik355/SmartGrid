package com.dominikbilik.smartgrid.measureddata.api.v1.dto.search;

import java.util.List;

public class FoundRecords {

    private Quantity quantity;
    private List<SearchRecord> values;
    private String error;

    public FoundRecords() {}

    public FoundRecords(String error) {
        this.error = error;
    }

    public FoundRecords(Quantity quantity, List<SearchRecord> values) {
        this.quantity = quantity;
        this.values = values;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public List<SearchRecord> getValues() {
        return values;
    }

    public void setValues(List<SearchRecord> values) {
        this.values = values;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "FoundRecords{" +
                "quantity=" + quantity +
                ", values=" + values +
                ", error='" + error + '\'' +
                '}';
    }
}
