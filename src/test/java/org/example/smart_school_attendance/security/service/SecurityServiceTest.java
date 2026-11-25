package org.example.smart_school_attendance.security.service;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.security.SecurityUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityServiceTest {

    private final SecurityService securityService = new SecurityService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static void loginAs(Role role, Long schoolId) {
        User user = TestData.user(5L, role, "5");
        if (schoolId != null) {
            user.setSchool(TestData.school(schoolId, "1"));
        }
        SecurityUser principal = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @Test
    void returnsCurrentUserData() {
        loginAs(Role.SCHOOL_ADMIN, 1L);

        assertThat(securityService.getCurrentUserId()).isEqualTo(5L);
        assertThat(securityService.getCurrentUserRole()).isEqualTo(Role.SCHOOL_ADMIN);
        assertThat(securityService.getCurrentUserSchoolId()).isEqualTo(1L);
        assertThat(securityService.isSystemAdmin()).isFalse();
    }

    @Test
    void noAuthenticationThrows() {
        assertThatThrownBy(securityService::getCurrentUser).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void systemAdminHasAccessToAnySchool() {
        loginAs(Role.SYSTEM_ADMIN, null);

        assertThat(securityService.hasAccessToSchoolUpdate(42L)).isTrue();
    }

    @Test
    void schoolAdminHasAccessOnlyToOwnSchool() {
        loginAs(Role.SCHOOL_ADMIN, 1L);

        assertThat(securityService.hasAccessToSchoolUpdate(1L)).isTrue();
        assertThat(securityService.hasAccessToSchoolUpdate(2L)).isFalse();
        assertThatCode(() -> securityService.validateSchoolAccess(1L)).doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.validateSchoolAccess(2L)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void teacherOfSameSchoolHasNoAccess() {
        loginAs(Role.TEACHER, 1L);

        assertThat(securityService.hasAccessToSchoolUpdate(1L)).isFalse();
    }

    @Test
    void nullSchoolIdHasNoAccess() {
        loginAs(Role.SYSTEM_ADMIN, null);

        assertThat(securityService.hasAccessToSchoolUpdate(null)).isFalse();
    }
}
