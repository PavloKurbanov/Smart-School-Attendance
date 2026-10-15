package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildDetailsRepository extends JpaRepository<ChildDetails, Long> {

//    @Query("SELECT c.parents FROM ChildDetails c WHERE c.email = :email")
//    List<User> findParentsByChildEmail(@Param("email") String email);
}
