package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existByPhoneNumber(String phoneNumber);
}
