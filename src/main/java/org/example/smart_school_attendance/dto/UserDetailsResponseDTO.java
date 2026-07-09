package org.example.smart_school_attendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.smart_school_attendance.entity.user.Role;

import java.time.LocalDateTime;

public record UserDetailsResponseDTO(
        Long id,
        String firstName,
        String lastName,
        int age,
        String email,
        String password,
        String confirmPassword,
        Role role,
        LocalDateTime createDate,
        LocalDateTime updateDate
) {
}
