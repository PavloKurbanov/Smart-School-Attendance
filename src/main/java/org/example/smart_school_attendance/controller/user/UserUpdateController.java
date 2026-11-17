package org.example.smart_school_attendance.controller.user;

import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.user.request.UserChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePasswordRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePhoneRequestDTO;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.user.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserUpdateController {
    private final UserUpdateService userUpdateService;
    private final SecurityService securityService;

    @Autowired
    public UserUpdateController(UserUpdateService userUpdateService, SecurityService securityService) {
        this.userUpdateService = userUpdateService;
        this.securityService = securityService;
    }

    @PutMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    public void updatePhone(
            @RequestBody @Valid UserChangePhoneRequestDTO userChangePhoneRequestDTO) {
        Long id = securityService.getCurrentUserId();
        userUpdateService.changePhoneNumber(id, userChangePhoneRequestDTO);
    }

    @PutMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public void updateEmail(
            @RequestBody @Valid UserChangeEmailRequestDTO userChangeEmailRequestDTO) {
        Long id = securityService.getCurrentUserId();
        userUpdateService.changeEmail(id, userChangeEmailRequestDTO);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(
            @RequestBody @Valid UserChangePasswordRequestDTO userChangePasswordRequestDTO) {
        Long id = securityService.getCurrentUserId();
        userUpdateService.changePassword(id, userChangePasswordRequestDTO);
    }
}
