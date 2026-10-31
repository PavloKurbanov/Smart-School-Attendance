package org.example.smart_school_attendance.dto.user.updatePasswordRequest;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(
        @NotBlank(message = "Current password can't be blank")
        String currentPassword,
        @NotBlank(message = "New password can't be blank")
        String newPassword,
        @NotBlank(message = "Confirm password can't be blank")
        String confirmNewPassword
) {
}
