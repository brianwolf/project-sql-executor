package org.toyota.sqlexecutor.configs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdviceCustom extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AppException.class })
    public ResponseEntity<Object> handleAppException(AppException ex, WebRequest request) {
        return ResponseEntity.status(409).body(ex.toResponse());
    }
}
