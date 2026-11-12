package org.example.smart_school_attendance.dto.school.response;

import java.time.LocalDate;

public record SchoolDetailsResponseDTO(
        Long id,
        String name,
        String address,
        String phoneNumber,
        String email,
        Double latitude,
        Double longitude,
        Integer radius,
        LocalDate createDate,
        LocalDate updateDate
) {
}
