package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.child.ChildDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildDetailsRepository extends JpaRepository<ChildDetails, Long> {
}
