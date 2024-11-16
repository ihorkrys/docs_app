package edu.duan.app.store.exception;

public class ItemStockNotFoundException extends RuntimeException {
    public ItemStockNotFoundException(String message) {
        super(message);
    }
}
