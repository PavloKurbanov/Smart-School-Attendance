package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.school.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
}
