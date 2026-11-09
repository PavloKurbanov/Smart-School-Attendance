package org.example.smart_school_attendance.dto.school.updatePhoneRequest;

import jakarta.validation.constraints.NotBlank;

public record ChangePhoneDTO(
        @NotBlank(message = "New phone number must not be blank")
        String newPhoneNumber
) {
}
