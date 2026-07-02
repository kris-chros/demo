package com.example.demo.exception;

// Extending RuntimeException means you don't have to clutter your method signatures with 'throws'
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
