package com.dominikbilik.smartgrid.datainput.saga;


import com.dominikbilik.smartgrid.common.model.Message;

public interface MessageSupplier<M extends Message> {

    public M getMessage();
    public String getKey();

}