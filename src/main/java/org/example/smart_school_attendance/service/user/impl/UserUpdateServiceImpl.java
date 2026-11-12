package org.example.smart_school_attendance.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.user.request.UserChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePasswordRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePhoneRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserUpdateDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.user.UserService;
import org.example.smart_school_attendance.service.user.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    @Autowired
    public UserUpdateServiceImpl(UserService userService, PasswordEncoder passwordEncoder, SecurityService securityService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public void changePhoneNumber(Long userId, UserChangePhoneRequestDTO request) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }

        if (request.phoneNumber() == null || request.phoneNumber().length() != 13) {
            throw new IllegalArgumentException("New Phone Number must have 13 characters");
        }

        log.info("Attempting to change phone number for user with id: {} to new phone number: {}", userId, request.phoneNumber());
        hasAccess(userId);
        userService.update(userId, UserUpdateDTO.builder().phoneNumber(request.phoneNumber()).build());
        log.info("Successfully changed phone number for user with id: {} to new phone number: {}", userId, request.phoneNumber());
    }

    @Override
    @Transactional
    public void changeEmail(Long userId, UserChangeEmailRequestDTO request) {
        if(userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }

        if (request.email() == null || !request.email().contains("@") || !request.email().contains(".") || request.email().isBlank()) {
            throw new IllegalArgumentException("New Email must be valid and not blank");
        }

        log.info("Attempting to change email for user with id: {} to new email: {}", userId, request.email());

        hasAccess(userId);

        User user = userService.entityById(userId);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Current passwords do not match");
        }
        userService.update(userId, UserUpdateDTO.builder().email(request.email()).build());
        log.info("Successfully changed email for user with id: {} to new email: {}", userId, request.email());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, UserChangePasswordRequestDTO request) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }

        if (request.newPassword() == null || request.newPassword().isBlank()) {
            throw new IllegalArgumentException("Passwords can't be null or blank");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        if (request.currentPassword() == null || request.currentPassword().isBlank()) {
            throw new IllegalArgumentException("Current passwords can't be null or blank");
        }
        log.info("Attempting to change password for user with id: {}", userId);

        hasAccess(userId);

        User user = userService.entityById(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            log.warn("Failed password change attempt for user id: {} - incorrect current password", userId);
            throw new IllegalArgumentException("Current password do not match");
        }

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        userService.update(userId, UserUpdateDTO.builder().password(encodedPassword).build());

        log.info("Successfully changed password for user with id: {}", userId);
    }

    private void hasAccess(Long userId) {
        SecurityUser currentUser = securityService.getCurrentUser();

        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new AccessDeniedException("Access denied: You do not have permission to update user with ID: " + userId);
        }
    }
}
