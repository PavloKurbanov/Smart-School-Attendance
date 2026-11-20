package org.example.smart_school_attendance.dto.childDetails.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateChildDetailsRequestDTO(
        @NotNull Long childId,
        @NotNull Long schoolId,
        @NotNull Long classRoomId,
        List<Long> parentIds
) {
}
