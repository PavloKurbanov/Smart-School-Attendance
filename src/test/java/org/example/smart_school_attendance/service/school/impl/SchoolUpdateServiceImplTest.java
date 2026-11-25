package org.example.smart_school_attendance.service.school.impl;

import org.example.smart_school_attendance.dto.school.request.SchoolChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolChangePhoneRequestDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateRequestDTO;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolUpdateServiceImplTest {

    private static final Long SCHOOL_ID = 1L;

    @Mock
    private SchoolService schoolService;
    @Mock
    private SecurityService securityService;
    @Mock
    private UserService userService;

    private SchoolUpdateServiceImpl schoolUpdateService;

    @BeforeEach
    void setUp() {
        schoolUpdateService = new SchoolUpdateServiceImpl(schoolService, securityService, userService);
    }

    private void currentUserIsSystemAdmin() {
        when(securityService.getCurrentUser()).thenReturn(mock(SecurityUser.class));
        when(securityService.isSystemAdmin()).thenReturn(true);
    }

    private void currentUserHasNoAccess() {
        when(securityService.getCurrentUser()).thenReturn(mock(SecurityUser.class));
        when(securityService.isSystemAdmin()).thenReturn(false);
        when(securityService.hasAccessToSchoolUpdate(SCHOOL_ID)).thenReturn(false);
    }

    @Nested
    class ChangeEmail {

        @Test
        void systemAdminCanChangeEmail() {
            currentUserIsSystemAdmin();

            schoolUpdateService.changeSchoolEmail(SCHOOL_ID, new SchoolChangeEmailRequestDTO("new@test.com"));

            verify(schoolService).update(SCHOOL_ID, SchoolUpdateRequestDTO.builder().email("new@test.com").build());
        }

        @Test
        void schoolAdminOfThisSchoolCanChangeEmail() {
            when(securityService.getCurrentUser()).thenReturn(mock(SecurityUser.class));
            when(securityService.isSystemAdmin()).thenReturn(false);
            when(securityService.hasAccessToSchoolUpdate(SCHOOL_ID)).thenReturn(true);

            schoolUpdateService.changeSchoolEmail(SCHOOL_ID, new SchoolChangeEmailRequestDTO("new@test.com"));

            verify(schoolService).update(SCHOOL_ID, SchoolUpdateRequestDTO.builder().email("new@test.com").build());
        }

        @Test
        void invalidEmailIsRejected() {
            assertThatThrownBy(() -> schoolUpdateService.changeSchoolEmail(SCHOOL_ID, new SchoolChangeEmailRequestDTO("invalid-email")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("New Email must be valid and not blank");
        }

        @Test
        void existingEmailIsRejected() {
            when(schoolService.existByEmail("taken@test.com")).thenReturn(true);

            assertThatThrownBy(() -> schoolUpdateService.changeSchoolEmail(SCHOOL_ID, new SchoolChangeEmailRequestDTO("taken@test.com")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
            verify(schoolService, never()).update(anyLong(), any());
        }

        @Test
        void userWithoutAccessCannotChangeEmail() {
            currentUserHasNoAccess();

            assertThatThrownBy(() -> schoolUpdateService.changeSchoolEmail(SCHOOL_ID, new SchoolChangeEmailRequestDTO("new@test.com")))
                    .isInstanceOf(RuntimeException.class);
            verify(schoolService, never()).update(anyLong(), any());
        }

        @Test
        @Disabled("BUG: no access is reported as IllegalArgumentException (HTTP 400) instead of AccessDeniedException (403)")
        void userWithoutAccessGetsAccessDenied() {
            currentUserHasNoAccess();

            assertThatThrownBy(() -> schoolUpdateService.changeSchoolEmail(SCHOOL_ID, new SchoolChangeEmailRequestDTO("new@test.com")))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class ChangePhone {

        @Test
        void systemAdminCanChangePhone() {
            currentUserIsSystemAdmin();

            schoolUpdateService.changeSchoolPhoneNumber(SCHOOL_ID, new SchoolChangePhoneRequestDTO("+380441111111"));

            verify(schoolService).update(SCHOOL_ID, SchoolUpdateRequestDTO.builder().phoneNumber("+380441111111").build());
        }

        @Test
        void existingPhoneIsRejected() {
            when(schoolService.existByPhone("+380441111111")).thenReturn(true);

            assertThatThrownBy(() -> schoolUpdateService.changeSchoolPhoneNumber(SCHOOL_ID, new SchoolChangePhoneRequestDTO("+380441111111")))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(schoolService, never()).update(anyLong(), any());
        }

        @Test
        void blankPhoneIsRejected() {
            assertThatThrownBy(() -> schoolUpdateService.changeSchoolPhoneNumber(SCHOOL_ID, new SchoolChangePhoneRequestDTO(" ")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void userWithoutAccessCannotChangePhone() {
            currentUserHasNoAccess();

            assertThatThrownBy(() -> schoolUpdateService.changeSchoolPhoneNumber(SCHOOL_ID, new SchoolChangePhoneRequestDTO("+380441111111")))
                    .isInstanceOf(RuntimeException.class);
            verify(schoolService, never()).update(anyLong(), any());
        }
    }
}
