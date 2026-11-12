package org.example.smart_school_attendance.dto.school.request;

import jakarta.validation.constraints.NotBlank;

public record SchoolChangePhoneRequestDTO(
        @NotBlank(message = "New phone number must not be blank")
        String newPhoneNumber
) {
}
