package com.userregistration.dto;

import com.userregistration.validation.PasswordStrength;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
        @NotBlank(message = "Username must not be blank")
        String username,
        @NotBlank(message = "Password must not be blank")
        @PasswordStrength
        String password,
        @NotBlank(message = "IP address must not be blank")
        String ipAddress) {
}
