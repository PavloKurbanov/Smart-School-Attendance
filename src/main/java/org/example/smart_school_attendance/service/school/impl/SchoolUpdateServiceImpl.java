package org.example.smart_school_attendance.service.school.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.school.SchoolUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchoolUpdateServiceImpl implements SchoolUpdateService {

    private final SchoolService schoolService;

    @Autowired
    public SchoolUpdateServiceImpl(SchoolService schoolService) {
        this.schoolService = schoolService;
    }


    @Override
    public void changeSchoolEmail(Long id, String newEmail) {
        // Implementation for changing school email
    }

    @Override
    public void changeSchoolPhoneNumber(Long id, String newPhoneNumber) {
        // Implementation for changing school phone number
    }
}
