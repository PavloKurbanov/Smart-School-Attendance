package org.example.smart_school_attendance.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Email for login is required")
        @Email
        String email,
        @NotBlank(message = "Password for login is required")
        String password
) {
}
