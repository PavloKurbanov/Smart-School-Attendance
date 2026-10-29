package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.school.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);

    Optional<School> findByAddress(String address);

    Optional<School> findByEmail(String email);

    Optional<School> findByPhoneNumber(String phoneNumber);

    boolean existsByAddress(String address);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
