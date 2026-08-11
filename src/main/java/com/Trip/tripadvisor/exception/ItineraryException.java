package com.Trip.tripadvisor.exception;

import org.springframework.http.HttpStatus;

public class ItineraryException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public ItineraryException(String message) {
        super(message);
        this.errorCode = null;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ItineraryException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public ItineraryException(String message, String errorCode, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}