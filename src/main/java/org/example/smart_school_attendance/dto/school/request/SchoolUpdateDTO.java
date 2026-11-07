package org.example.smart_school_attendance.dto.school.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SchoolUpdateDTO(
        @NotNull(message = "School ID is required")
        Long id,
        String phoneNumber,
        String email
) {
}
