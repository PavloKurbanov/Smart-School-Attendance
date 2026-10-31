package org.example.smart_school_attendance.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserUpdateDTO(
        @NotNull Long id,
        @NotBlank String phoneNumber,
        @NotBlank String email,
        @NotBlank String password
) {
}
