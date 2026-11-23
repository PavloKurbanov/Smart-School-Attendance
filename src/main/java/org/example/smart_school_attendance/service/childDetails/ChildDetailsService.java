package org.example.smart_school_attendance.service.childDetails;

import org.example.smart_school_attendance.dto.childDetails.response.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.childDetails.request.CreateChildDetailsRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;

import java.util.List;

public interface ChildDetailsService {
    ChildDetailsResponseDTO createChildDetails(CreateChildDetailsRequestDTO request);
    List<ChildDetails> findAllById(List<Long> ids);
}
