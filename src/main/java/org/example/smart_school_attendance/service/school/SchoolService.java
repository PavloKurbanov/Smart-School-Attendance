package org.example.smart_school_attendance.service.school;

import org.example.smart_school_attendance.dto.school.request.SchoolCreateDetailsDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchoolService {
    SchoolDetailsResponseDTO create(SchoolCreateDetailsDTO responseDTO);

    void update(Long id, SchoolUpdateDTO schoolUpdateDTO);

    SchoolDetailsResponseDTO findById(Long id);

    SchoolDetailsResponseDTO findByName(String name);

    SchoolDetailsResponseDTO findByEmail(String email);

    SchoolDetailsResponseDTO findByPhoneNumber(String phoneNumber);

    Page<SchoolDetailsResponseDTO> findAll(Pageable pageable);
}
