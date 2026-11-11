package org.example.smart_school_attendance.dto.user.request;

import lombok.Builder;

@Builder
public record UserUpdateDTO(
        String phoneNumber,
        String email,
        String password
) {
}
