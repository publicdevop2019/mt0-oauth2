package com.hw.shared.validation;

public class ValidationErrorException extends RuntimeException {
    public ValidationErrorException(Throwable cause) {
        super("error during validation api call", cause);
    }
}
