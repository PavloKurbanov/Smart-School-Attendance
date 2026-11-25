package org.example.smart_school_attendance.service.user.impl;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.user.request.UserChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePasswordRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePhoneRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserUpdateDTO;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUpdateServiceImplTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityService securityService;

    private UserUpdateServiceImpl userUpdateService;
    private User user;

    @BeforeEach
    void setUp() {
        userUpdateService = new UserUpdateServiceImpl(userService, passwordEncoder, securityService);
        user = TestData.user(USER_ID, Role.PARENT, "1");
    }

    @Nested
    class ChangePhone {

        @Test
        void updatesPhoneOfCurrentUser() {
            when(securityService.getCurrentUserId()).thenReturn(USER_ID);

            userUpdateService.changePhoneNumber(USER_ID, new UserChangePhoneRequestDTO("+380671234567"));

            verify(userService).update(USER_ID, UserUpdateDTO.builder().phoneNumber("+380671234567").build());
        }

        @Test
        void phoneMustHaveThirteenCharacters() {
            assertThatThrownBy(() -> userUpdateService.changePhoneNumber(USER_ID, new UserChangePhoneRequestDTO("+38067")))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(userService, never()).update(anyLong(), any());
        }

        @Test
        void otherUserCannotBeUpdated() {
            when(securityService.getCurrentUserId()).thenReturn(2L);

            assertThatThrownBy(() -> userUpdateService.changePhoneNumber(USER_ID, new UserChangePhoneRequestDTO("+380671234567")))
                    .isInstanceOf(AccessDeniedException.class);
            verify(userService, never()).update(anyLong(), any());
        }
    }

    @Nested
    class ChangeEmail {

        @Test
        void updatesEmailWhenPasswordMatches() {
            when(securityService.getCurrentUserId()).thenReturn(USER_ID);
            when(userService.entityById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("secret", user.getPassword())).thenReturn(true);

            userUpdateService.changeEmail(USER_ID, new UserChangeEmailRequestDTO("new@test.com", "secret"));

            verify(userService).update(USER_ID, UserUpdateDTO.builder().email("new@test.com").build());
        }

        @Test
        void wrongPasswordIsRejected() {
            when(securityService.getCurrentUserId()).thenReturn(USER_ID);
            when(userService.entityById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userUpdateService.changeEmail(USER_ID, new UserChangeEmailRequestDTO("new@test.com", "wrong")))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(userService, never()).update(anyLong(), any());
        }

        @Test
        void sameEmailIsRejected() {
            when(securityService.getCurrentUserId()).thenReturn(USER_ID);
            when(userService.entityById(USER_ID)).thenReturn(user);

            assertThatThrownBy(() -> userUpdateService.changeEmail(USER_ID, new UserChangeEmailRequestDTO(user.getEmail(), "secret")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("same");
        }

        @Test
        void invalidEmailIsRejected() {
            assertThatThrownBy(() -> userUpdateService.changeEmail(USER_ID, new UserChangeEmailRequestDTO("not-an-email", "secret")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void otherUserCannotBeUpdated() {
            when(securityService.getCurrentUserId()).thenReturn(2L);

            assertThatThrownBy(() -> userUpdateService.changeEmail(USER_ID, new UserChangeEmailRequestDTO("new@test.com", "secret")))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class ChangePassword {

        @Test
        void storesEncodedNewPassword() {
            when(securityService.getCurrentUserId()).thenReturn(USER_ID);
            when(userService.entityById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode("new-pass")).thenReturn("ENCODED-NEW");

            userUpdateService.changePassword(USER_ID, new UserChangePasswordRequestDTO("old", "new-pass", "new-pass"));

            verify(userService).update(USER_ID, UserUpdateDTO.builder().password("ENCODED-NEW").build());
        }

        @Test
        void confirmationMismatchIsRejected() {
            assertThatThrownBy(() -> userUpdateService.changePassword(USER_ID, new UserChangePasswordRequestDTO("old", "new-pass", "other")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("New passwords do not match");
        }

        @Test
        void wrongCurrentPasswordIsRejected() {
            when(securityService.getCurrentUserId()).thenReturn(USER_ID);
            when(userService.entityById(USER_ID)).thenReturn(user);
            when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userUpdateService.changePassword(USER_ID, new UserChangePasswordRequestDTO("wrong", "new-pass", "new-pass")))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(userService, never()).update(anyLong(), any());
        }

        @Test
        void invalidUserIdIsRejected() {
            assertThatThrownBy(() -> userUpdateService.changePassword(0L, new UserChangePasswordRequestDTO("old", "new-pass", "new-pass")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
