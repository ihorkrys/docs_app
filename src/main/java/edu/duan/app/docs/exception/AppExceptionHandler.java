package edu.duan.app.docs.exception;

import edu.duan.app.docs.api.AppErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody AppErrorResponse handleDocumentNotFoundException(DocumentNotFoundException e) {
        return new AppErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
