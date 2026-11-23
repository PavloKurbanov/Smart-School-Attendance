package org.example.smart_school_attendance.dto.classRoom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateClassRoomRequestDTO(
        @NotBlank String name,
        List<Long> children,
        @NotNull Long teacherId,
        @NotNull Long schoolId,
        @NotNull Integer capacity
) {
}
