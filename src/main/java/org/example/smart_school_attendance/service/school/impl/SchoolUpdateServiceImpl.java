package org.example.smart_school_attendance.service.school.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateRequestDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolChangeEmailRequestDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolChangePhoneRequestDTO;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.school.SchoolUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchoolUpdateServiceImpl implements SchoolUpdateService {

    private final SchoolService schoolService;
    private final SecurityService securityService;

    @Autowired
    public SchoolUpdateServiceImpl(SchoolService schoolService, SecurityService securityService) {
        this.schoolService = schoolService;
        this.securityService = securityService;
    }


    @Override
    public void changeSchoolEmail(Long id, SchoolChangeEmailRequestDTO schoolChangeEmailRequestDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (schoolChangeEmailRequestDTO == null || schoolChangeEmailRequestDTO.newEmail() == null || !schoolChangeEmailRequestDTO.newEmail().contains("@") || !schoolChangeEmailRequestDTO.newEmail().contains(".") || schoolChangeEmailRequestDTO.newEmail().isBlank()) {
            throw new IllegalArgumentException("New Email must be valid and not blank");
        }

        if(accessUserToSchoolUpdate(id)) {
            throw new IllegalArgumentException("User does not have permission to update school with ID: " + id);
        }

        log.debug("Attempting to change email for school with id: {} to new email: {}", id, schoolChangeEmailRequestDTO.newEmail());
        schoolService.update(id, SchoolUpdateRequestDTO.builder().email(schoolChangeEmailRequestDTO.newEmail()).build());
        log.info("Successfully changed email for school with id: {} to new email: {}", id, schoolChangeEmailRequestDTO.newEmail());
    }

    @Override
    public void changeSchoolPhoneNumber(Long id, SchoolChangePhoneRequestDTO schoolChangePhoneRequestDTO) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (schoolChangePhoneRequestDTO == null || schoolChangePhoneRequestDTO.newPhoneNumber() == null || schoolChangePhoneRequestDTO.newPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("New Phone Number must not be blank");
        }

        if(accessUserToSchoolUpdate(id)) {
            throw new IllegalArgumentException("User does not have permission to update school with ID: " + id);
        }

        log.debug("Attempting to change phone number for school with id: {} to new phone number: {}", id, schoolChangePhoneRequestDTO.newPhoneNumber());
        schoolService.update(id, SchoolUpdateRequestDTO.builder().phoneNumber(schoolChangePhoneRequestDTO.newPhoneNumber()).build());
        log.info("Successfully changed phone number for school with id: {} to new phone number: {}", id, schoolChangePhoneRequestDTO.newPhoneNumber());
    }

    private boolean accessUserToSchoolUpdate(Long schoolId) {
        SecurityUser currentUser = securityService.getCurrentUser();
        if (securityService.isSystemAdmin()) {
            log.debug("User with ID: {} is a system admin, proceeding with update", currentUser.getId());
            return true;
        } else if (!securityService.hasAccessToSchoolUpdate(schoolId)) {
            throw new IllegalArgumentException("User with ID: " + currentUser.getId() + " does not have permission to update school with ID: " + schoolId);
        }
        return true;
    }
}