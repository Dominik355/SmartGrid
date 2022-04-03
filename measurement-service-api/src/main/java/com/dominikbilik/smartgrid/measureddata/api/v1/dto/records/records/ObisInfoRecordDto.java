package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records.records;

public class ObisInfoRecordDto extends RecordDto {

    private String C;
    private String D;
    private String E;
    private String F;
    private String description;

    private ObisInfoRecordDto() {}

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
