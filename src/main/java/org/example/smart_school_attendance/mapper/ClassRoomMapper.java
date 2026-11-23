package org.example.smart_school_attendance.mapper;

import org.example.smart_school_attendance.dto.classRoom.ClassRoomDetailsResponseDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassRoomMapper {
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "schoolId", source = "school.id")
    @Mapping(target = "childIds", expression = "java(mapChildIds(classRoom.getChildren()))")
    ClassRoomDetailsResponseDTO classRoomDetailsResponseDTO(ClassRoom classRoom);

    default List<Long> mapChildIds(List<ChildDetails> children) {
        if (children == null) {
            return List.of();
        }
        return children.stream().map(ChildDetails::getId).toList();
    }
}
