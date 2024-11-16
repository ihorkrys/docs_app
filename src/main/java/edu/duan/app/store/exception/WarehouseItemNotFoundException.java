package edu.duan.app.store.exception;

public class WarehouseItemNotFoundException extends RuntimeException {
    public WarehouseItemNotFoundException(String message) {
        super(message);
    }
}
