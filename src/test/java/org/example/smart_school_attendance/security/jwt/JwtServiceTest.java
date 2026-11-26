package org.example.smart_school_attendance.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.security.UserAuthDto;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.example.smart_school_attendance.security.SecurityUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    /** Same value as JwtService.SECRET_KEY (it is private). */
    static final String SECRET_KEY = "9a72df6554c123456789abcdef0123456789abcdef0123456789abcdef012345";

    private final JwtService jwtService = new JwtService();

    private static UserAuthDto auth(User user, UserStatus status) {
        return new UserAuthDto(user.getId(), user.getEmail(), user.getRole(), status);
    }

    static String expiredToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis() - 120_000))
                .setExpiration(new Date(System.currentTimeMillis() - 60_000))
                .addClaims(Map.of("active", "ACTIVE"))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void tokenContainsUserClaims() {
        User user = TestData.user(3L, Role.TEACHER, "3");
        String token = jwtService.generateTokenUser(auth(user, UserStatus.ACTIVE));

        assertThat(jwtService.extractUserName(token)).isEqualTo(user.getEmail());
        assertThat(jwtService.extractClaim(token, "userId")).isEqualTo("3");
        assertThat(jwtService.extractClaim(token, "role")).isEqualTo("TEACHER");
        assertThat(jwtService.extractClaim(token, "active")).isEqualTo("ACTIVE");
    }

    @Test
    void tokenOfActiveUserIsValid() {
        User user = TestData.user(3L, Role.TEACHER, "3");
        String token = jwtService.generateTokenUser(auth(user, UserStatus.ACTIVE));

        assertThat(jwtService.isTokenValid(token, new SecurityUser(user))).isTrue();
    }

    @Test
    void tokenOfPendingUserIsNotValid() {
        User user = TestData.user(3L, Role.PARENT, "3");
        String token = jwtService.generateTokenUser(auth(user, UserStatus.PENDING));

        assertThat(jwtService.isTokenValid(token, new SecurityUser(user))).isFalse();
    }

    @Test
    void tokenOfAnotherUserIsNotValid() {
        User user = TestData.user(3L, Role.TEACHER, "3");
        User other = TestData.user(4L, Role.TEACHER, "4");
        String token = jwtService.generateTokenUser(auth(user, UserStatus.ACTIVE));

        assertThat(jwtService.isTokenValid(token, new SecurityUser(other))).isFalse();
    }

    @Test
    void expiredTokenThrows() {
        User user = TestData.user(3L, Role.TEACHER, "3");

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken(user.getEmail()), new SecurityUser(user)))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @Disabled("BUG (security): status is read from the token, not the DB. A user DISABLED by admin keeps access until the token expires (24h)")
    void tokenOfUserDisabledAfterLoginIsNotValid() {
        User user = TestData.user(3L, Role.TEACHER, "3");
        String token = jwtService.generateTokenUser(auth(user, UserStatus.ACTIVE));
        user.setUserStatus(UserStatus.DISABLED);

        assertThat(jwtService.isTokenValid(token, new SecurityUser(user))).isFalse();
    }
}
