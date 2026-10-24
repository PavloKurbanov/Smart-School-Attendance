package org.example.smart_school_attendance.dto.school.response;

public record SchoolDetailsResponseDTO(
        String name,
        String address,
        String phoneNumber,
        String email,
        Double latitude,
        Double longitude,
        Integer radius
) {
}
