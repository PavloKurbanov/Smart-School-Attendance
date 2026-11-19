package org.example.smart_school_attendance.dto.school.request;

import jakarta.validation.constraints.NotBlank;

public record SchoolChangeEmailRequestDTO(
        @NotBlank(message = "New email must not be blank")
        String newEmail
) {

}
