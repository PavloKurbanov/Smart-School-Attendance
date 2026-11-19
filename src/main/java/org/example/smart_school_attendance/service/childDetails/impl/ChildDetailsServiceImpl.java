package org.example.smart_school_attendance.service.childDetails.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.childDetails.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.childDetails.CreateChildDetailsRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.mapper.ChildDetailsMapper;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.service.childDetails.ChildDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Slf4j
public class ChildDetailsServiceImpl implements ChildDetailsService {

    private final ChildDetailsRepository childDetailsRepository;
    private final ChildDetailsMapper childDetailsMapper;

    @Autowired
    public ChildDetailsServiceImpl(ChildDetailsRepository childDetailsRepository, ChildDetailsMapper childDetailsMapper) {
        this.childDetailsRepository = childDetailsRepository;
        this.childDetailsMapper = childDetailsMapper;
    }

    @Override
    @Transactional
    public ChildDetailsResponseDTO createChildDetails(CreateChildDetailsRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Create Child details can't be null");
        }
        log.debug("Attempting to create new child details with id: {}, school: {}, class room: {}"
                , request.childId(), request.schoolId(), request.classRoomId());

        ChildDetails childDetails = new ChildDetails(
                request.childId(),
                request.schoolId(),
                request.classRoomId(),
                //new ArrayList<>(request.parentIds()));

        childDetailsRepository.save(childDetails);
        return childDetailsMapper.toChildDetailsResponseDTO(childDetails);
    }
}
