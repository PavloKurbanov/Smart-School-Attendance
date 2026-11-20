package org.example.smart_school_attendance.dto.childDetails.response;

public record ChildDetailsResponseDTO(
        Long id,
        String childName,
        String schoolName,
        String classRoomName,
        String parentsNames
) {
}
