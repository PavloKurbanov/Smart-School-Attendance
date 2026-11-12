package org.example.smart_school_attendance.service.school.impl;

import org.example.smart_school_attendance.dto.school.request.SchoolChangeEmailRequestDTO;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchoolUpdateServiceImplTest {
    private SchoolUpdateServiceImpl schoolUpdateService;

    public SchoolUpdateServiceImplTest() {
        SchoolService schoolService = Mockito.mock(SchoolService.class);
        SecurityService securityService = Mockito.mock(SecurityService.class);
        schoolUpdateService = new SchoolUpdateServiceImpl(schoolService, securityService);
    }

    @Test
    void testChangeSchoolEmailWrongEmail() {
        Long schoolId = 1L;
        SchoolChangeEmailRequestDTO requestDTO = new SchoolChangeEmailRequestDTO("invalid-email");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schoolUpdateService.changeSchoolEmail(schoolId, requestDTO);
        });

        assertEquals("New Email must be valid and not blank", exception.getMessage());
    }
}