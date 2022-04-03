package com.dominikbilik.smartgrid.datainput.saga;

import com.dominikbilik.smartgrid.common.model.Message;

public class MessageSupplierBuilder<M extends Message> {

    private M message;
    private String key;

    public MessageSupplierBuilder withMessage(M message) {
        this.message = message;
        return this;
    }
    public MessageSupplierBuilder withKey(String key) {
        this.key = key;
        return this;
    }

    public MessageSupplier<M> build() {
        return new MessageSupplier() {
            @Override
            public M getMessage() {
                return message;
            }

            @Override
            public String getKey() {
                return key;
            }
        };
    }

}
