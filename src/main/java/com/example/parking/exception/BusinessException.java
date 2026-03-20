package com.example.parking.exception;

/** Signals a business rule violation. */
public class BusinessException extends RuntimeException {

    /** Creates a business exception. */
    public BusinessException(String message) {
        super(message);
    }
}
