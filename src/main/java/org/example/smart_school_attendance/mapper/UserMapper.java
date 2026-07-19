package org.example.smart_school_attendance.mapper;

import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.response.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDetailsResponseDTO toUserDetailsResponseDTO(User user);

    UserListResponseDTO toUserListResponseDTO(User user);
}
