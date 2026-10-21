package org.example.smart_school_attendance.service.school;

import org.example.smart_school_attendance.entity.school.School;

public interface SchoolService {
    School create();

    School update(School school);

    School findById(Long id);

    School findByName(String name);

    School findByEmail(String email);

    School findByPhoneNumber(String phoneNumber);
}
