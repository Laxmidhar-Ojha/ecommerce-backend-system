package com.ecommerce.exception;

public class BadRequestException extends RuntimeException {
    // This exception is thrown when the client sends a bad request, such as invalid input data
    public BadRequestException(String message) {
        super(message);
    }
}