package org.example.smart_school_attendance;

import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;

/**
 * Builders for test entities and requests. Pass {@code id = null} for entities that will be persisted.
 */
public final class TestData {

    private TestData() {
    }

    public static School school(Long id, String suffix) {
        School school = new School(
                "School " + suffix,
                "Address " + suffix,
                phone(suffix),
                "school" + suffix + "@test.com",
                50.45,
                30.52,
                200
        );
        school.setId(id);
        return school;
    }

    public static User user(Long id, Role role, String suffix) {
        User user = new User("First" + suffix, "Last" + suffix, 30,
                "user" + suffix + "@test.com", phone(suffix), "encoded-password", role);
        user.setId(id);
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    public static PublicRegisterRequestDTO registerRequest(String suffix, Long schoolId) {
        return new PublicRegisterRequestDTO("First" + suffix, "Last" + suffix, 30,
                "user" + suffix + "@test.com", phone(suffix), "password" + suffix, schoolId);
    }

    public static AdminCreateUserRequestDTO adminCreateRequest(String suffix, Role role, Long schoolId) {
        return new AdminCreateUserRequestDTO("First" + suffix, "Last" + suffix, 30,
                "user" + suffix + "@test.com", phone(suffix), "password" + suffix, role, schoolId);
    }

    /** 13-character phone number, unique per suffix (suffix must be 1-3 chars). */
    public static String phone(String suffix) {
        String base = "+380500000000";
        return base.substring(0, base.length() - suffix.length()) + suffix;
    }
}
