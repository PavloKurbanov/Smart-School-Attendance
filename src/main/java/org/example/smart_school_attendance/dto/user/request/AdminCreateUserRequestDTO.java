package org.example.smart_school_attendance.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.smart_school_attendance.entity.user.Role;

public record AdminCreateUserRequestDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Min(5) int age,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String password,
        @NotBlank Role role
) {
}
