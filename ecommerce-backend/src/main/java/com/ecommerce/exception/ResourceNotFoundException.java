package com.ecommerce.exception;

public class ResourceNotFoundException extends RuntimeException {
    // This exception is thrown when a requested resource (like a product, user,
    // order, etc.) is not found in the database
    public ResourceNotFoundException(String message) {
        super(message);
    }
}