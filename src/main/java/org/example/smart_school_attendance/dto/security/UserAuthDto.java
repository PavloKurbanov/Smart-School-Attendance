package org.example.smart_school_attendance.dto.security;

import org.example.smart_school_attendance.entity.user.Role;

public record UserAuthDto(
        Long id,
        String email,
        Role role
) {
}
