package com.ecommerce.exception;

public class ProductOutOfStockException extends RuntimeException {
    // This exception is thrown when a user tries to add a product to the cart or
    // place an order, but the product is out of stock
    public ProductOutOfStockException(String message) {
        super(message);
    }
}