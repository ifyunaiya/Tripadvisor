package com.Trip.tripadvisor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ItineraryException.class)
    public ResponseEntity<String> handleItineraryException(ItineraryException ex){
        return ResponseEntity
                .status(ex.getStatus())
                .body(ex.getMessage());
    }
}