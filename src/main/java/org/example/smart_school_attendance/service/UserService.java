package org.example.smart_school_attendance.service;

import org.example.smart_school_attendance.dto.CreateUserRequestDTO;
import org.example.smart_school_attendance.dto.UserDetailsResponseDTO;

public interface UserService {
    UserDetailsResponseDTO create(CreateUserRequestDTO request);
}
