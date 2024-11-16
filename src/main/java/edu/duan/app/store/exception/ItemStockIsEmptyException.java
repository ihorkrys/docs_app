package edu.duan.app.store.exception;

public class ItemStockIsEmptyException extends RuntimeException {
    public ItemStockIsEmptyException(String message) {
        super(message);
    }
}
