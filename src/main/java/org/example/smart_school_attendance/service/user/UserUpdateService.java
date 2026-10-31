package org.example.smart_school_attendance.service.user;

public interface UserUpdateService {

    void changePhoneNumber(Long userId, String newPhoneNumber);

    void changeEmail(Long userId, String newEmail, String password);

    void changePassword(Long userId, String currentPassword, String newPassword, String confirmationPassword);
}