package org.example.smart_school_attendance.service.childDetails;

import org.example.smart_school_attendance.dto.childDetails.response.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.childDetails.request.CreateChildDetailsRequestDTO;

public interface ChildDetailsService {
    ChildDetailsResponseDTO createChildDetails(CreateChildDetailsRequestDTO request);
}
