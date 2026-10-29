package org.example.smart_school_attendance.dto.user.request;

public interface UserRegistrationRequest {
    String firstName();
    String lastName();
    int age();
    String email();
    String phoneNumber();
    String password();
}
