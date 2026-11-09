package org.example.smart_school_attendance.dto.school.updateEmailRequest;

import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDTO(
        @NotBlank(message = "New email must not be blank")
        String newEmail
) {

}
