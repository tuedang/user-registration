package com.userregistration.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordStrengthValidatorTest {

    private PasswordStrengthValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordStrengthValidator();
    }

    @Test
    void isValid_shouldReturnFalse_whenPasswordIsNull() {
        assertThat(validator.isValid(null, null)).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenPasswordTooShort() {
        assertThat(validator.isValid("Short1A", null)).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenNoDigit() {
        assertThat(validator.isValid("OnlylettersX", null)).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenNoUppercase() {
        assertThat(validator.isValid("lowercase9chars", null)).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenPasswordMeetsRules() {
        assertThat(validator.isValid("Validpass9", null)).isTrue();
    }
}
