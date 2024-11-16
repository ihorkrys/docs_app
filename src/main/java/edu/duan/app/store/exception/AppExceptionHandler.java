package edu.duan.app.store.exception;

import edu.duan.app.store.api.AppErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody AppErrorResponse handleOrderNotFoundException(OrderNotFoundException e) {
        return new AppErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(UnsupportedStateOfOrderException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody AppErrorResponse handleUnsupportedStateOfOrderException(UnsupportedStateOfOrderException e) {
        return new AppErrorResponse(HttpStatus.PRECONDITION_FAILED.value(), e.getMessage());
    }


    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody AppErrorResponse handleItemNotFoundException(ItemNotFoundException e) {
        return new AppErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(ItemStockNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody AppErrorResponse handleItemStockNotFoundException(ItemStockNotFoundException e) {
        return new AppErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
    @ExceptionHandler(ItemStockIsEmptyException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public @ResponseBody AppErrorResponse handleItemStockIsEmptyException(ItemStockIsEmptyException e) {
        return new AppErrorResponse(HttpStatus.PRECONDITION_FAILED.value(), e.getMessage());
    }
    @ExceptionHandler(WarehouseItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody AppErrorResponse handleItemStockNotFoundException(WarehouseItemNotFoundException e) {
        return new AppErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
