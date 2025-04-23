package com.example.Task.Management.System.exceptions;

import java.util.List;

public class DomainValidationException extends RuntimeException {
    private final List<String> errors;

    public DomainValidationException(List<String> errors) {
        super("Domain validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
