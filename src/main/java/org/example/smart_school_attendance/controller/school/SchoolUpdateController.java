package org.example.smart_school_attendance.controller.school;

import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.school.updateEmailRequest.ChangeEmailDTO;
import org.example.smart_school_attendance.dto.school.updatePhoneRequest.ChangePhoneDTO;
import org.example.smart_school_attendance.service.school.SchoolUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/school/update")
public class SchoolUpdateController {

    private final SchoolUpdateService schoolUpdateService;

    @Autowired
    public SchoolUpdateController(SchoolUpdateService schoolUpdateService) {
        this.schoolUpdateService = schoolUpdateService;
    }

    @PutMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public void updatePhone(
            @RequestBody @Valid Long id, ChangePhoneDTO changePhoneDTO) {
        schoolUpdateService.changeSchoolPhoneNumber(id, changePhoneDTO);
    }

    @PutMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public void updateEmail(
            @RequestBody @Valid Long id, ChangeEmailDTO changeEmailDTO) {
        schoolUpdateService.changeSchoolEmail(id, changeEmailDTO);
    }
}
