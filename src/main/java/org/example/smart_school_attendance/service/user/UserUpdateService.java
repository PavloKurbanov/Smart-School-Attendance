package org.example.smart_school_attendance.service.user;

import org.example.smart_school_attendance.dto.user.request.UserChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePasswordRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePhoneRequestDTO;

public interface UserUpdateService {

    void changePhoneNumber(Long userId, UserChangePhoneRequestDTO request);

    void changeEmail(Long userId, UserChangeEmailRequestDTO request);

    void changePassword(Long userId, UserChangePasswordRequestDTO request);
}