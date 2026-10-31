package org.example.smart_school_attendance.dto.user.updateEmailRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDTO(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,
        @NotBlank
        String password
) {
}
