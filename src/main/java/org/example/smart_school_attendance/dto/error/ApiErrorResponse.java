package org.example.smart_school_attendance.dto.error;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime time,
        int status,
        String error,
        String message,
        String path
) {
}
