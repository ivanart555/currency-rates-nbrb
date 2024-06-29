package com.vadarod.exception;

public class RateServiceException extends RuntimeException {
    public RateServiceException(String message) {
        super(message);
    }

    public RateServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}