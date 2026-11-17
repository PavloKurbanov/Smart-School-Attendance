package org.example.smart_school_attendance.mapper;

import org.example.smart_school_attendance.dto.childDetails.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChildDetailsMapper {
    ChildDetailsResponseDTO toChildDetailsResponseDTO(ChildDetails childDetails);
}
