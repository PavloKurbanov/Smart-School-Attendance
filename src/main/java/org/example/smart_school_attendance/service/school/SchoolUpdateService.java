package org.example.smart_school_attendance.service.school;

import org.example.smart_school_attendance.dto.school.request.SchoolChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolChangePhoneRequestDTO;

public interface SchoolUpdateService {
    void changeSchoolEmail(Long id, SchoolChangeEmailRequestDTO schoolChangeEmailRequestDTO);

    void changeSchoolPhoneNumber(Long id, SchoolChangePhoneRequestDTO schoolChangePhoneRequestDTO);
}
