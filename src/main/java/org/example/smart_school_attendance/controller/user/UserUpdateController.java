package org.example.smart_school_attendance.controller.user;

import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.user.request.UserChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePasswordRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserChangePhoneRequestDTO;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.service.user.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserUpdateController {
    private final UserUpdateService userUpdateService;

    @Autowired
    public UserUpdateController(UserUpdateService userUpdateService) {
        this.userUpdateService = userUpdateService;
    }

    @PutMapping("{id}/phone")
    @ResponseStatus(HttpStatus.OK)
    public void updatePhone(
            @PathVariable Long id,
            @RequestBody @Valid UserChangePhoneRequestDTO userChangePhoneRequestDTO) {
        userUpdateService.changePhoneNumber(id, userChangePhoneRequestDTO);
    }

    @PutMapping("/{id}/email")
    @ResponseStatus(HttpStatus.OK)
    public void updateEmail(
            @PathVariable Long id,
            @RequestBody @Valid UserChangeEmailRequestDTO userChangeEmailRequestDTO) {
        userUpdateService.changeEmail(id, userChangeEmailRequestDTO);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(
            @PathVariable Long id,
            @RequestBody @Valid UserChangePasswordRequestDTO userChangePasswordRequestDTO) {
        userUpdateService.changePassword(id, userChangePasswordRequestDTO);
    }
}
