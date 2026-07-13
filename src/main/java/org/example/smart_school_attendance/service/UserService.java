package org.example.smart_school_attendance.service;

import org.example.smart_school_attendance.dto.CreateUserRequestDTO;
import org.example.smart_school_attendance.dto.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDetailsResponseDTO create(CreateUserRequestDTO request);

    void update(User user);

    User findById(Long id);

    Page<UserListResponseDTO> findAll(Pageable pageable);

    boolean existByEmail(String email);

    boolean existByPhone(String phoneNumber);

    UserDetailsResponseDTO findByEntityId(Long id);
}
