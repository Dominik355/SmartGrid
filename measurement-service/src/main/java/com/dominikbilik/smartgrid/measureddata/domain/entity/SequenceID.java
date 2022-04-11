package com.dominikbilik.smartgrid.measureddata.domain.entity;

/**
 * Just for entities, which want to have their ID generated from Sequencer
 */
public interface SequenceID {

    public void setId(Long id);

    public Long getId();

    public String getTablename();

}
