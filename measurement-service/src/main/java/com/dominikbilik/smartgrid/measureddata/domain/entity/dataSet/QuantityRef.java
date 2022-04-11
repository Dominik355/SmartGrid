package com.dominikbilik.smartgrid.measureddata.domain.entity.dataSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(QuantityRef.TABLE_NAME)
public class QuantityRef {

    public static final String TABLE_NAME = "dataset_quantity_joins";

    @Column("device_data_set")
    private Long datasetId;
    @Column("quantity_detail")
    private Long quantityDetailId;

    public QuantityRef(Long datasetId, Long quantityDetailId) {
        this.datasetId = datasetId;
        this.quantityDetailId = quantityDetailId;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public Long getQuantityDetailId() {
        return quantityDetailId;
    }

    public void setQuantityDetailId(Long quantityDetailId) {
        this.quantityDetailId = quantityDetailId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof QuantityRef)) return false;

        QuantityRef that = (QuantityRef) o;

        return new EqualsBuilder()
                .append(getDatasetId(), that.getDatasetId())
                .append(getQuantityDetailId(), that.getQuantityDetailId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                    .append(getDatasetId())
                    .append(getQuantityDetailId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "QuantityRef{" +
                "datasetId=" + datasetId +
                ", quantityDetailId=" + quantityDetailId +
                '}';
    }
}
