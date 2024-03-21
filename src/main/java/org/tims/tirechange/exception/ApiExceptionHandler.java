package org.tims.tirechange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceAccessException.class)
    protected ResponseEntity<Object> handleResourceAccessException(ResourceAccessException ex, WebRequest request) {
        String bodyOfResponse = "Could not connect to the tire change service. 503 Service Unavailable. Please try again later.";
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String bodyOfResponse = "An unexpected error occurred. Please try again later.";
        return handleExceptionInternal(ex, bodyOfResponse, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(NoAvailableTimeslotsException.class)
    public ResponseEntity<Object> handleNoAvailableTimeslotsException(
            NoAvailableTimeslotsException ex, WebRequest request) {

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), new ArrayList<>());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

}
