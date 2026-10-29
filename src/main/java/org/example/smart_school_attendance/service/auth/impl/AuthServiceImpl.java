package org.example.smart_school_attendance.service.auth.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.security.UserAuthDto;
import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
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

        UserAuthDto user = userService.authenticate(request.email(), request.password());

        log.info("User with email: {}, ID: {}, Role: {} successfully logged in"
                , request.email(), user.id(), user.role().name().toUpperCase());
        return jwtService.generateTokenUser(user);
    }


    @Override
    @Transactional
    public String registration(PublicRegisterRequestDTO publicRegisterRequestDTO) {
        if (publicRegisterRequestDTO == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }
        log.debug("Attempting to register with email: {}, phone number: {}", publicRegisterRequestDTO.email()
                , publicRegisterRequestDTO.phoneNumber());

        UserDetailsResponseDTO user = userService.create(publicRegisterRequestDTO);

        UserAuthDto userAuthDto = new UserAuthDto(user.id(), user.email(), user.role());

        log.info("User with email: {}, ID: {}, Role: {} successfully registered"
                , user.email(), user.id(), user.role().name().toUpperCase());
        return jwtService.generateTokenUser(userAuthDto);
    }

    @Override
    @Transactional
    public String registrationByAdmin(AdminCreateUserRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }
        log.debug("Attempting to register Role: {} with email: {}, phone number: {}", requestDTO.role().name().toUpperCase(), requestDTO.email()
                , requestDTO.phoneNumber());

        UserDetailsResponseDTO user = userService.createUserByAdmin(requestDTO);

        UserAuthDto userAuthDto = new UserAuthDto(user.id(), user.email(), user.role());

        log.info("User with email: {}, ID: {}, Role: {} successfully registered"
                , user.email(), user.id(), user.role().name().toUpperCase());
        return jwtService.generateTokenUser(userAuthDto);
    }
}