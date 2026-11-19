package org.example.smart_school_attendance.dto.school.request;

import lombok.Builder;

@Builder
public record SchoolUpdateRequestDTO(
        String phoneNumber,
        String email
) {
}
