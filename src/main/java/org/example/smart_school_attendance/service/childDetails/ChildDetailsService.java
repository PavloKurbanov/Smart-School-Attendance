package org.example.smart_school_attendance.service.childDetails;

import org.example.smart_school_attendance.dto.childDetails.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.childDetails.CreateChildDetailsRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;

public interface ChildDetailsService {
    ChildDetailsResponseDTO createChildDetails(CreateChildDetailsRequestDTO request);
}
