package org.example.smart_school_attendance.service.school;

public interface SchoolUpdateService {
    void changeSchoolEmail(Long id, String newEmail);

    void changeSchoolPhoneNumber(Long id, String newPhoneNumber);
}
