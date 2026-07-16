package org.example.smart_school_attendance.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.smart_school_attendance.entity.user.Role;

public record CreateUserRequestDTO(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @Min(value = 5)
        int age,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String phoneNumber,
        @NotBlank
        String password,
        @NotBlank
        String confirmPassword,
        @NotNull
        Role role
) {
}
