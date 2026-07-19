package org.example.smart_school_attendance.service.auth;

import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.user.request.CreateUserRequestDTO;

public interface AuthService {
    String login(LoginRequestDTO request);

    String registration(CreateUserRequestDTO request);
}
