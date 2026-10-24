package org.example.smart_school_attendance.mapper;

import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

    SchoolDetailsResponseDTO toSchoolDetailsResponseDTO(School school);
}
