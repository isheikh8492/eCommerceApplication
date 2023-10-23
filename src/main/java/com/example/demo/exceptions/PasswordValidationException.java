package com.example.demo.exceptions;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException(String exception) {
        super(exception);
    }
}
