package org.example.smart_school_attendance.service.childDetails.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.childDetails.response.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.childDetails.request.CreateChildDetailsRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.ChildDetailsMapper;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.service.childDetails.ChildDetailsService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ChildDetailsServiceImpl implements ChildDetailsService {

    private final ChildDetailsRepository childDetailsRepository;
    private final ChildDetailsMapper childDetailsMapper;
    private final UserService userService;
    private final SchoolService schoolService;

    @Autowired
    public ChildDetailsServiceImpl(ChildDetailsRepository childDetailsRepository, ChildDetailsMapper childDetailsMapper, UserService userService, SchoolService schoolService) {
        this.childDetailsRepository = childDetailsRepository;
        this.childDetailsMapper = childDetailsMapper;
        this.userService = userService;
        this.schoolService = schoolService;
    }

    @Override
    @Transactional
    public ChildDetailsResponseDTO createChildDetails(CreateChildDetailsRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Create Child details can't be null");
        }
        log.debug("Attempting to create new child details with id: {}, school: {}, class room: {}"
                , request.childId(), request.schoolId(), request.classRoomId());

        User child = userService.entityById(request.childId());

        School school = schoolService.entityById(request.schoolId());

        ClassRoom classRoom = new ClassRoom();


        ChildDetails childDetails = new ChildDetails(
                child,
                school,
                classRoom,
                userService.findAllByIds(request.parentIds())
        );

        childDetailsRepository.save(childDetails);
        return childDetailsMapper.toChildDetailsResponseDTO(childDetails);
    }
}
