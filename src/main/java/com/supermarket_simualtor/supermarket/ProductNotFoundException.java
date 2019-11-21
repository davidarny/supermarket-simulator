package com.supermarket_simualtor.supermarket;

public class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}
