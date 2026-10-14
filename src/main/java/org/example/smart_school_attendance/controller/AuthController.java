package org.example.smart_school_attendance.controller;

import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.user.request.CreateUserRequestDTO;
import org.example.smart_school_attendance.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String login(@RequestBody LoginRequestDTO requestDTO) {
        return authService.login(requestDTO);
    }

    @PostMapping("/register")
    public String registration(
            @RequestBody CreateUserRequestDTO requestDTO) {
        return authService.registration(requestDTO);
    }
}
