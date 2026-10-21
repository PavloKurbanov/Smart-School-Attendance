package org.example.smart_school_attendance.service.childDetails.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.childDetails.CreateChildDetailsDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.service.childDetails.ChildDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChildDetailsServiceImpl implements ChildDetailsService {

    private final ChildDetailsRepository childDetailsRepository;

    @Autowired
    public ChildDetailsServiceImpl(ChildDetailsRepository childDetailsRepository) {
        this.childDetailsRepository = childDetailsRepository;
    }

    @Override
    public ChildDetails createChildDetails(CreateChildDetailsDTO request) {
        if(request == null){
            throw new IllegalArgumentException("Create Child details can't be null");
        }
        log.debug("Attempting to create new child details with id: {}, school: {}, class room: {}"
                , request.child().getId(), request.school().getName(), request.classRoom().getName());

        if(childDetailsRepository.existsById(request.child().getId())) {
            throw new IllegalArgumentException("Child details with id: " + request.child().getId() + " already exists");
        }


    }
}
