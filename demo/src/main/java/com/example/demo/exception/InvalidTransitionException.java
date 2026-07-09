package com.example.demo.exception;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(String message) {
        super(message);
    }
}
