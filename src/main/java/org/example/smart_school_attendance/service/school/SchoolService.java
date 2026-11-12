package org.example.smart_school_attendance.service.school;

import org.example.smart_school_attendance.dto.school.request.SchoolCreateRequestTO;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateRequestDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchoolService {
    SchoolDetailsResponseDTO create(SchoolCreateRequestTO responseDTO);

    void update(Long id, SchoolUpdateRequestDTO schoolUpdateRequestDTO);

    SchoolDetailsResponseDTO findById(Long id);

    SchoolDetailsResponseDTO findByName(String name);

    SchoolDetailsResponseDTO findByEmail(String email);

    SchoolDetailsResponseDTO findByPhoneNumber(String phoneNumber);

    Page<SchoolDetailsResponseDTO> findAll(Pageable pageable);

    School entityById(Long id);
}
