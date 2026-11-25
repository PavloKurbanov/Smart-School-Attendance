package org.example.smart_school_attendance.service.classRoom;

import jakarta.persistence.EntityNotFoundException;
import org.example.smart_school_attendance.dto.classRoom.ClassRoomDetailsResponseDTO;
import org.example.smart_school_attendance.dto.classRoom.CreateClassRoomRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.ClassRoomMapper;
import org.example.smart_school_attendance.repository.ClassRoomRepository;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ClassRoomServiceImpl implements ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final ClassRoomMapper classRoomMapper;
    private final UserService userService;
    private final SchoolService schoolService;
    private final ChildDetailsRepository childDetailsRepository;


    @Autowired
    public ClassRoomServiceImpl(ClassRoomRepository classRoomRepository, ClassRoomMapper classRoomMapper, UserService userService, SchoolService schoolService, ChildDetailsRepository childDetailsRepository) {
        this.classRoomRepository = classRoomRepository;
        this.classRoomMapper = classRoomMapper;
        this.userService = userService;
        this.schoolService = schoolService;
        this.childDetailsRepository = childDetailsRepository;
    }

    @Override
    @Transactional
    public ClassRoomDetailsResponseDTO createClassRoom(CreateClassRoomRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Class room request can't be null");
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Class room name can't be empty");
        }

        if (request.capacity() == null || request.capacity() < 10) {
            throw new IllegalArgumentException("Class room capacity can't be less than 10");
        }

        User teacher = userService.entityById(request.teacherId());

        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User with ID: " + request.teacherId() + " is not a teacher");
        }

        School school = schoolService.entityById(request.schoolId());

        // Repository instead of ChildDetailsService: ChildDetailsService depends on ClassRoomService (bean cycle).
        List<ChildDetails> foundChildren = request.children() == null || request.children().isEmpty()
                ? List.of()
                : childDetailsRepository.findAllById(request.children());

        if (request.children() != null && !request.children().isEmpty()) {
            Set<Long> foundIds = foundChildren.stream()
                    .map(ChildDetails::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = request.children().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            if (!missingIds.isEmpty()) {
                throw new EntityNotFoundException("Children not found with ids: " + missingIds);
            }
        }

        if (foundChildren.size() > request.capacity()) {
            throw new IllegalArgumentException("Class room capacity can't be greater than " + request.capacity());
        }

        ClassRoom classRoom = new ClassRoom(
                request.name(),
                new ArrayList<>(),
                teacher,
                school,
                request.capacity()
        );

        for (ChildDetails child : foundChildren) {
            child.setClassRoom(classRoom);
            classRoom.getChildren().add(child);
        }

        ClassRoom savedClassRoom = classRoomRepository.save(classRoom);
        return classRoomMapper.classRoomDetailsResponseDTO(savedClassRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassRoomDetailsResponseDTO getClassRoomById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Class room id can't be less than 0");
        }
        return classRoomRepository.findById(id).map(classRoomMapper::classRoomDetailsResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Class room with id: " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ClassRoom entityById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Class room id can't be less than 0");
        }
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Class room with id: " + id + " not found"));
    }


}
