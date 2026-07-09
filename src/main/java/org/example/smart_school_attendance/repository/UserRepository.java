package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
