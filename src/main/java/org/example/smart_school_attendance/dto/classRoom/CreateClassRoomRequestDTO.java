package org.example.smart_school_attendance.dto.classRoom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.user.User;

import java.util.List;

public record CreateClassRoomRequestDTO(
        @NotBlank String name,
        List<Long> children,
        @NotNull Long teacherId,
        @NotNull Long schoolId
) {
}
