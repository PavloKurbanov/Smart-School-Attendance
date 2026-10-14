package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.child.ChildDetails;
import org.example.smart_school_attendance.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildDetailsRepository extends JpaRepository<ChildDetails, Long> {

    @Query("SELECT c.parents FROM ChildDetails c WHERE c.email = :email")
    List<User> findParentsByChildEmail(@Param("email") String email);
}
