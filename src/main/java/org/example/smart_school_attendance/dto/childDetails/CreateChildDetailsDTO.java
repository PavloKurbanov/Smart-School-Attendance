package org.example.smart_school_attendance.dto.childDetails;

import jakarta.validation.constraints.NotBlank;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.User;

import java.util.List;

public record CreateChildDetailsDTO(
        @NotBlank User child,
        @NotBlank School school,
        @NotBlank ClassRoom classRoom,
        @NotBlank List<User> parents
) {
}
