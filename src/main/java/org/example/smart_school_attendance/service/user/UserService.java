package org.example.smart_school_attendance.service.user;

import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User create(PublicRegisterRequestDTO request);

    User createUserByAdmin(AdminCreateUserRequestDTO request);

    void update(User user);

    User findById(Long id);

    User findByEmail(String email);

    Page<UserListResponseDTO> findAll(Pageable pageable);

    boolean existByEmail(String email);

    boolean existByPhone(String phoneNumber);
}
