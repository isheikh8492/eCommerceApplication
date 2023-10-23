package com.example.demo.exceptions;

public class UsernameNotFoundException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Username not found.";

    public UsernameNotFoundException() {
        super(EXCEPTION_MESSAGE);
    }

    public UsernameNotFoundException(String message) {
        super(message);
    }
}
