package com.userregistration.service;

public class RegistrationRuleException extends RuntimeException {

    private final String field;

    public RegistrationRuleException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
