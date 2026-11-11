package org.example.smart_school_attendance.service.school.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateDTO;
import org.example.smart_school_attendance.dto.school.updateEmailRequest.ChangeEmailDTO;
import org.example.smart_school_attendance.dto.school.updatePhoneRequest.ChangePhoneDTO;
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
    public void changeSchoolEmail(Long id, ChangeEmailDTO changeEmailDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (changeEmailDTO == null || changeEmailDTO.newEmail() == null || !changeEmailDTO.newEmail().contains("@") || !changeEmailDTO.newEmail().contains(".") || changeEmailDTO.newEmail().isBlank()) {
            throw new IllegalArgumentException("New Email must be valid and not blank");
        }
        log.debug("Attempting to change email for school with id: {} to new email: {}", id, changeEmailDTO.newEmail());
        schoolService.update(id, SchoolUpdateDTO.builder().email(changeEmailDTO.newEmail()).build());
        log.info("Successfully changed email for school with id: {} to new email: {}", id, changeEmailDTO.newEmail());
    }

    @Override
    public void changeSchoolPhoneNumber(Long id, ChangePhoneDTO changePhoneDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (changePhoneDTO == null || changePhoneDTO.newPhoneNumber() == null || changePhoneDTO.newPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("New Phone Number must not be blank");
        }
        log.debug("Attempting to change phone number for school with id: {} to new phone number: {}", id, changePhoneDTO.newPhoneNumber());
        schoolService.update(id, SchoolUpdateDTO.builder().phoneNumber(changePhoneDTO.newPhoneNumber()).build());
        log.info("Successfully changed phone number for school with id: {} to new phone number: {}", id, changePhoneDTO.newPhoneNumber());
    }
}