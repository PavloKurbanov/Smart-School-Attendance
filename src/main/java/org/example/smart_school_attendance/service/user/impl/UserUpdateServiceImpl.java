package org.example.smart_school_attendance.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.user.request.UserUpdateDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.service.user.UserService;
import org.example.smart_school_attendance.service.user.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserUpdateServiceImpl(UserService userService,  PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePhoneNumber(Long userId, String newPhoneNumber) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }

        if (newPhoneNumber == null || newPhoneNumber.length() != 11) {
            throw new IllegalArgumentException("New Phone Number must have 11 characters");
        }
        log.info("Attempting to change phone number for user with id: {} to new phone number: {}", userId, newPhoneNumber);
        userService.update(UserUpdateDTO.builder().id(userId).phoneNumber(newPhoneNumber).build());
        log.info("Successfully changed phone number for user with id: {} to new phone number: {}", userId, newPhoneNumber);
    }

    @Override
    @Transactional
    public void changeEmail(Long userId, String newEmail, String password) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }

        if(newEmail == null || !newEmail.contains("@") ||  !newEmail.contains(".") || newEmail.isBlank()) {
            throw new IllegalArgumentException("New Email must be valid and not blank");
        }
        log.info("Attempting to change email for user with id: {} to new email: {}", userId, newEmail);
        User user = userService.entityById(userId);

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Current passwords do not match");
        }
        userService.update(UserUpdateDTO.builder().id(userId).email(newEmail).build());
        log.info("Successfully changed email for user with id: {} to new email: {}", userId, newEmail);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmationPassword) {
        if(userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }

        if(newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Passwords can't be null or blank");
        }

        if(!newPassword.equals(confirmationPassword)) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        if(currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Current passwords can't be null or blank");
        }
        log.info("Attempting to change password for user with id: {}", userId);

        User user = userService.entityById(userId);
        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Failed password change attempt for user id: {} - incorrect current password", userId);
            throw new IllegalArgumentException("Current password do not match");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        userService.update(UserUpdateDTO.builder().id(userId).password(encodedPassword).build());

        log.info("Successfully changed password for user with id: {}", userId);
    }
}
