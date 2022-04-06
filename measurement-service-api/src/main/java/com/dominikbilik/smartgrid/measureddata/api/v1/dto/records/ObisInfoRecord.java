package com.dominikbilik.smartgrid.measureddata.api.v1.dto.records;

public class ObisInfoRecord extends Record {

    /**
     Obis codes discribing state/actions of a device can have a numeric or character values.
     Take a look at regex pattern used to split line of an obis code record
     Usually the character is placed at index C, but to keep consistency  in records and to
     cover needs of other devices we rather use String for all the values.
     (Yes, it's inefficient, but safe in this scenario)
     */
    private String C;
    private String D;
    private String E;
    private String F;
    private String description;

    private  ObisInfoRecord() {}

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

    public static class Builder {
        private ObisInfoRecord record;

        public Builder() {
            this.record = new ObisInfoRecord();
        }

        public Builder withC(String value) {
            record.setC(value);
            return this;
        }

        public Builder withD(String value) {
            record.setD(value);
            return this;
        }

        public Builder withE(String value) {
            record.setE(value);
            return this;
        }

        public Builder withF(String value) {
            record.setF(value);
            return this;
        }

        public Builder withDescription(String value) {
            record.setDescription(value);
            return this;
        }

        public ObisInfoRecord build() {
            return this.record;
        }
    }
}
