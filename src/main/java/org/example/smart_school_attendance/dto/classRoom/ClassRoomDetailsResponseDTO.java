package org.example.smart_school_attendance.dto.classRoom;

import java.time.LocalDateTime;
import java.util.List;

public record ClassRoomDetailsResponseDTO(
        Long id,
        String name,
        Long teacherId,
        Long schoolId,
        Integer capacity,
        List<Long> childIds,
        LocalDateTime createDate,
        LocalDateTime updateDate
) {
}
