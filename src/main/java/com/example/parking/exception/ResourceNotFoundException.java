package com.example.parking.exception;

/** Signals that a required resource was not found. */
public class ResourceNotFoundException extends RuntimeException {

    /** Creates a resource not found exception. */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
