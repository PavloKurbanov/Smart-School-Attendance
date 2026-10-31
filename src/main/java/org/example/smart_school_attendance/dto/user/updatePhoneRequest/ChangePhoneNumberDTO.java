package org.example.smart_school_attendance.dto.user.updatePhoneRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePhoneNumberDTO(
        @NotBlank(message = "Phone number cannot be blank")
        @Size(min = 11, max = 11, message = "Phone number must be at least 11 characters long")
        String phoneNumber
) {
}
