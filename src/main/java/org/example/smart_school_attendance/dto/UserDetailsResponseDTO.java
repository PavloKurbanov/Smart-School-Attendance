package org.example.smart_school_attendance.dto;

import org.example.smart_school_attendance.entity.user.Role;

import java.time.LocalDateTime;

public record UserDetailsResponseDTO(
        Long id,
        String firstName,
        String lastName,
        int age,
        String email,
        String phoneNumber,
        Role role,
        LocalDateTime createDate,
        LocalDateTime updateDate
) {
}
