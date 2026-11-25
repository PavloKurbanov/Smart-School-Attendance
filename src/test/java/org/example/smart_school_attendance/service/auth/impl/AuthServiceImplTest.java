package org.example.smart_school_attendance.service.auth.impl;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.security.UserAuthDto;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.example.smart_school_attendance.security.jwt.JwtService;
import org.example.smart_school_attendance.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(jwtService, userService);
    }

    @Test
    void loginReturnsTokenForAuthenticatedUser() {
        UserAuthDto auth = new UserAuthDto(1L, "a@test.com", Role.TEACHER, UserStatus.ACTIVE);
        when(userService.authenticate("a@test.com", "secret")).thenReturn(auth);
        when(jwtService.generateTokenUser(auth)).thenReturn("TOKEN");

        assertThat(authService.login(new LoginRequestDTO("a@test.com", "secret"))).isEqualTo("TOKEN");
    }

    @Test
    void loginWithBlankCredentialsIsRejected() {
        assertThatThrownBy(() -> authService.login(new LoginRequestDTO(" ", "secret"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("a@test.com", ""))).isInstanceOf(IllegalArgumentException.class);
        verify(userService, never()).authenticate(any(), any());
    }

    @Test
    void registrationReturnsTokenWithPendingStatus() {
        PublicRegisterRequestDTO request = TestData.registerRequest("1", 1L);
        UserDetailsResponseDTO created = new UserDetailsResponseDTO(7L, "F", "L", 30, request.email(), request.phoneNumber(),
                Role.PARENT, UserStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        when(userService.create(request)).thenReturn(created);
        when(jwtService.generateTokenUser(new UserAuthDto(7L, request.email(), Role.PARENT, UserStatus.PENDING))).thenReturn("TOKEN");

        assertThat(authService.registration(request)).isEqualTo("TOKEN");
    }

    @Test
    void registrationWithNullRequestIsRejected() {
        assertThatThrownBy(() -> authService.registration(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
