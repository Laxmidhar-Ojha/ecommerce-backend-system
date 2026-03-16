package com.ecommerce.exception;

public class UserAlreadyExistsException extends RuntimeException {
    // This exception is thrown when a user tries to register with an email that
    // already exists in the database
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}