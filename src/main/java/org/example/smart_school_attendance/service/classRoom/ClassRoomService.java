package org.example.smart_school_attendance.service.classRoom;

import org.example.smart_school_attendance.dto.classRoom.ClassRoomDetailsResponseDTO;
import org.example.smart_school_attendance.dto.classRoom.CreateClassRoomRequestDTO;

public interface ClassRoomService {
    ClassRoomDetailsResponseDTO createClassRoom(CreateClassRoomRequestDTO request);
    ClassRoomDetailsResponseDTO getClassRoomById(Long id);
}
