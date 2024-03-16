package org.tims.tirechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(value = { /* Your custom exceptions here */ })
//    public ResponseEntity<Object> handleException(Exception exception) {
//        // Implement error handling logic
//        return new ResponseEntity<>(/* Error response body */, HttpStatus.INTERNAL_SERVER_ERROR);
//    }


}
