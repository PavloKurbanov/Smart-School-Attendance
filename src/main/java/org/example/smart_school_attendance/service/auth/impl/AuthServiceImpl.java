package org.example.smart_school_attendance.service.auth.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.user.request.CreateUserRequestDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.security.jwt.JwtService;
import org.example.smart_school_attendance.service.auth.AuthService;
import org.example.smart_school_attendance.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthServiceImpl(JwtService jwtService, PasswordEncoder passwordEncoder, UserService userService) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    @Transactional
    public String login(LoginRequestDTO request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email can't be null or blank");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password can't be null or blank");
        }

        log.debug("Attempting to login with email: {}", request.email());
        User user = userService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        log.info("User with email: {}, ID: {}, Role: {} successfully logged in", request.email(), user.getId(), user.getRole().name().toUpperCase());
        return jwtService.generateTokenUser(user);
    }

    @Override
    @Transactional
    public String registration(CreateUserRequestDTO createUserRequestDTO) {
        if (createUserRequestDTO == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }
        log.debug("Attempting to register Role: {} with email: {}, phone number: {}", createUserRequestDTO.role().name().toUpperCase(), createUserRequestDTO.email()
                , createUserRequestDTO.phoneNumber());

        User user = userService.create(createUserRequestDTO);
        log.info("User with email: {}, ID: {}, Role: {} successfully registered", user.getEmail(), user.getId(), user.getRole().name().toUpperCase());
        return jwtService.generateTokenUser(user);
    }
}
