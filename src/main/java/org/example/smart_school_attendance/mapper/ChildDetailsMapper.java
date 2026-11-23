package org.example.smart_school_attendance.mapper;

import org.example.smart_school_attendance.dto.childDetails.response.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChildDetailsMapper {
    @Mapping(target = "childName", expression = "java(maoChildName(childDetails.getChild()))")
    @Mapping(target = "schoolName", source = "school.name")
    @Mapping(target = "classRoomName", source = "classRoom.name")
    @Mapping(target = "parentsNames", expression = "java(mapParents(childDetails.getParents()))")
    ChildDetailsResponseDTO toChildDetailsResponseDTO(ChildDetails childDetails);

    default String mapParents(List<User> parents) {
        if (parents == null || parents.isEmpty()) {
            return "";
        }
        return parents.stream()
                .filter(Objects::nonNull)
                .map(parent -> {
                    String first = parent.getFirstName() != null ? parent.getFirstName().trim() : "";
                    String last = parent.getLastName() != null ? parent.getLastName().trim() : "";
                    return (first + " " + last).trim();
                })
                .filter(fullName -> !fullName.isBlank())
                .collect(Collectors.joining(", "));
    }

    default String mapChildName(User child) {
        if (child == null) {
            return "";
        }
        String first = child.getFirstName() != null ? child.getFirstName().trim() : "";
        String last = child.getLastName() != null ? child.getLastName().trim() : "";
        return (first + " " + last).trim();
    }
}
