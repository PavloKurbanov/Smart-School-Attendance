package org.example.smart_school_attendance.controller.user;

import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.user.updateEmailRequest.ChangeEmailDTO;
import org.example.smart_school_attendance.dto.user.updatePasswordRequest.ChangePasswordDTO;
import org.example.smart_school_attendance.dto.user.updatePhoneRequest.ChangePhoneNumberDTO;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.service.user.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/update/me")
public class UserUpdateController {
    private final UserUpdateService userUpdateService;

    @Autowired
    public UserUpdateController(UserUpdateService userUpdateService) {
        this.userUpdateService = userUpdateService;
    }

    @PutMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    public void updatePhone(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody @Valid ChangePhoneNumberDTO changePhoneNumberDTO) {
        userUpdateService.changePhoneNumber(user.getId(), changePhoneNumberDTO.phoneNumber());
    }

    @PutMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public void updateEmail(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody @Valid ChangeEmailDTO changeEmailDTO) {
        userUpdateService.changeEmail(user.getId(), changeEmailDTO.email(), changeEmailDTO.password());
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userUpdateService.changePassword(user.getId(), changePasswordDTO.currentPassword()
                , changePasswordDTO.newPassword(), changePasswordDTO.confirmNewPassword());
    }
}
