package org.example.smart_school_attendance.service.school;

import org.example.smart_school_attendance.dto.school.request.SchoolCreateDetailsDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;

public interface SchoolService {
    School create(SchoolCreateDetailsDTO responseDTO);

    School update(School school);

    SchoolDetailsResponseDTO findById(Long id);

    SchoolDetailsResponseDTO findByName(String name);

    SchoolDetailsResponseDTO findByEmail(String email);

    SchoolDetailsResponseDTO findByPhoneNumber(String phoneNumber);
}
