package com.example.demo.exceptions;

public class InvalidUserIdException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Invalid user id.";

    public InvalidUserIdException() {
        super(EXCEPTION_MESSAGE);
    }
}
