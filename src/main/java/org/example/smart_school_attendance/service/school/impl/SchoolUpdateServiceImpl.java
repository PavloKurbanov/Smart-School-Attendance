package org.example.smart_school_attendance.service.school.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateDTO;
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
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (newEmail == null || !newEmail.contains("@") || !newEmail.contains(".") || newEmail.isBlank()) {
            throw new IllegalArgumentException("New Email must be valid and not blank");
        }
        log.debug("Attempting to change email for school with id: {} to new email: {}", id, newEmail);
        schoolService.update(SchoolUpdateDTO.builder().id(id).email(newEmail).build());
        log.info("Successfully changed email for school with id: {} to new email: {}", id, newEmail);
    }

    @Override
    public void changeSchoolPhoneNumber(Long id, String newPhoneNumber) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (newPhoneNumber == null || newPhoneNumber.isBlank()) {
            throw new IllegalArgumentException("New Phone Number must not be blank");
        }
        log.debug("Attempting to change phone number for school with id: {} to new phone number: {}", id, newPhoneNumber);
        schoolService.update(SchoolUpdateDTO.builder().id(id).phoneNumber(newPhoneNumber).build());
        log.info("Successfully changed phone number for school with id: {} to new phone number: {}", id, newPhoneNumber);
    }
}