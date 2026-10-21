package org.example.smart_school_attendance.service.childDetails;

import org.example.smart_school_attendance.dto.childDetails.CreateChildDetailsDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;

public interface ChildDetailsService {
    ChildDetails createChildDetails(CreateChildDetailsDTO request);
}
