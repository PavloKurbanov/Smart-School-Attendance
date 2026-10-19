package org.example.smart_school_attendance.controller;

import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        return authService.login(requestDTO);
    }

    @PostMapping("/register")
    public String registration(
            @Valid @RequestBody PublicRegisterRequestDTO requestDTO) {
        return authService.registration(requestDTO);
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> createUserByAdmin(@Valid @RequestBody AdminCreateUserRequestDTO requestDTO) {
        String user = authService.registrationByAdmin(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
