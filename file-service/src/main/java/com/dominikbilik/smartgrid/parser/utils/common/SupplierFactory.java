package com.dominikbilik.smartgrid.parser.utils.common;

import java.util.function.Supplier;

public class SupplierFactory<C> {

    private Supplier<C> supplier;

    public SupplierFactory(Supplier<C> supplier) {
        this.supplier = supplier;
    }

    public C get() {
        return supplier.get();
    }
}
