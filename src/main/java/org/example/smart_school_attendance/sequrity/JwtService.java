package org.example.smart_school_attendance.sequrity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.smart_school_attendance.entity.user.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtService {
    private static final String SECRET_KEY = "9a72df6554c123456789abcdef0123456789abcdef0123456789abcdef012345";
    public static final long EXPIRATION_TIME = 86400000;

    public String extractUsername(String token) {
        return extractClaim(token, null);
    }

    public String extractClaim(String token, String userName) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(SECRET_KEY);
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        if (userName == null) {
            return claims.getSubject();
        }
        return claims.get(userName).toString();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .addClaims(Map.of("userId", user.getId(), "email", user.getEmail(), "ROLE", "ROLE_USER"))
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(SECRET_KEY);
        Claims claim = jwtParser.parseClaimsJws(token).getBody();
        Date expiration = claim.getExpiration();
        return expiration.before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
