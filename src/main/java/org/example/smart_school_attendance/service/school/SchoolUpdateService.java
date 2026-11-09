package org.example.smart_school_attendance.service.school;

import org.example.smart_school_attendance.dto.school.updateEmailRequest.ChangeEmailDTO;
import org.example.smart_school_attendance.dto.school.updatePhoneRequest.ChangePhoneDTO;

public interface SchoolUpdateService {
    void changeSchoolEmail(Long id, ChangeEmailDTO changeEmailDTO);

    void changeSchoolPhoneNumber(Long id, ChangePhoneDTO changePhoneDTO);
}
