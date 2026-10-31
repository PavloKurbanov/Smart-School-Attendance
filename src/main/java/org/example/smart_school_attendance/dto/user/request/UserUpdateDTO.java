package org.example.smart_school_attendance.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserUpdateDTO(
        @NotNull Long id,
        String phoneNumber,
        String email,
        String password
) {
}
