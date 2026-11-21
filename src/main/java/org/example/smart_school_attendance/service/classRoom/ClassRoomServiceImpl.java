package org.example.smart_school_attendance.service.classRoom;

import org.example.smart_school_attendance.dto.classRoom.ClassRoomDetailsResponseDTO;
import org.example.smart_school_attendance.dto.classRoom.CreateClassRoomRequestDTO;
import org.example.smart_school_attendance.repository.ClassRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassRoomServiceImpl implements ClassRoomService {

    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public ClassRoomServiceImpl(ClassRoomRepository classRoomRepository) {
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    public ClassRoomDetailsResponseDTO createClassRoom(CreateClassRoomRequestDTO request) {

        return null;
    }

    @Override
    public ClassRoomDetailsResponseDTO getClassRoomById(Long id) {
        return null;
    }
}
