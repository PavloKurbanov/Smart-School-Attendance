package org.example.smart_school_attendance.security.jwt;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.security.UserAuthDto;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.security.service.UsersDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private UsersDetailsService usersDetailsService;
    private JwtAuthenticationFilter filter;
    private User user;

    @BeforeEach
    void setUp() {
        usersDetailsService = mock(UsersDetailsService.class);
        filter = new JwtAuthenticationFilter(usersDetailsService);
        user = TestData.user(3L, Role.SCHOOL_ADMIN, "3");
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/me");
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    @Test
    void validTokenAuthenticatesUserWithRoleAuthority() throws Exception {
        String token = new JwtService().generateTokenUser(new UserAuthDto(3L, user.getEmail(), Role.SCHOOL_ADMIN, UserStatus.ACTIVE));
        when(usersDetailsService.loadUserByUsername(user.getEmail())).thenReturn(new SecurityUser(user));
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(requestWithToken(token), new MockHttpServletResponse(), chain);

        assertThat(chain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(Object::toString).containsExactly("ROLE_SCHOOL_ADMIN");
    }

    @Test
    void requestWithoutTokenPassesUnauthenticated() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/users"), new MockHttpServletResponse(), chain);

        assertThat(chain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void pendingUserTokenDoesNotAuthenticate() throws Exception {
        String token = new JwtService().generateTokenUser(new UserAuthDto(3L, user.getEmail(), Role.PARENT, UserStatus.PENDING));
        when(usersDetailsService.loadUserByUsername(user.getEmail())).thenReturn(new SecurityUser(user));
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(requestWithToken(token), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @Disabled("BUG: expired/malformed JWT throws out of the filter (HTTP 500) instead of leaving the request unauthenticated (401/403)")
    void expiredTokenLeavesRequestUnauthenticated() {
        MockFilterChain chain = new MockFilterChain();

        assertThatCode(() -> filter.doFilter(requestWithToken(JwtServiceTest.expiredToken(user.getEmail())), new MockHttpServletResponse(), chain))
                .doesNotThrowAnyException();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @Disabled("BUG: token for an email that no longer exists throws UsernameNotFoundException out of the filter (HTTP 500)")
    void tokenOfDeletedUserLeavesRequestUnauthenticated() {
        String token = new JwtService().generateTokenUser(new UserAuthDto(3L, user.getEmail(), Role.PARENT, UserStatus.ACTIVE));
        when(usersDetailsService.loadUserByUsername(user.getEmail()))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("gone"));

        assertThatCode(() -> filter.doFilter(requestWithToken(token), new MockHttpServletResponse(), new MockFilterChain()))
                .doesNotThrowAnyException();
    }
}
