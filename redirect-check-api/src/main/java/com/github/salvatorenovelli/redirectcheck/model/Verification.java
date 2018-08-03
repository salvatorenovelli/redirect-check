package com.github.salvatorenovelli.redirectcheck.model;

import java.util.function.Consumer;


class Verification {
    private final CheckedSupplier supplier;

    public Verification(CheckedSupplier supplier) {
        this.supplier = supplier;
    }

    public static Verification of(CheckedSupplier supplier) {
        return new Verification(supplier);
    }

    public boolean onException(Consumer<String> errorMessageConsumer) {
        try {
            return supplier.apply();
        } catch (Exception e) {
            errorMessageConsumer.accept(e.getMessage());
            return false;
        }
    }

    public interface CheckedSupplier {
        boolean apply() throws Exception;
    }
}
