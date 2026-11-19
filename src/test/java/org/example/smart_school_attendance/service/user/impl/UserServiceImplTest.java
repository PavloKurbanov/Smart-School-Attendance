package org.example.smart_school_attendance.service.user.impl;

import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.UserMapper;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UserServiceImplTest {
    private final UserServiceImpl userService;
    private final SchoolService schoolService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImplTest() {
        this.schoolService = Mockito.mock(SchoolService.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
        this.userMapper = Mockito.mock(UserMapper.class);

        this.userService = new UserServiceImpl(userRepository, passwordEncoder, userMapper, schoolService);
    }

    @Test
    void successCreateUserByAdmin() {
        School mockSchool = new School();
        mockSchool.setId(1L);

        AdminCreateUserRequestDTO requestDTO = new AdminCreateUserRequestDTO(
                "Pavlo", "Kurbanov", 24, "kurbanovpavlo@gmail.com", "+380981221493"
                , "270119Pavlo", Role.SCHOOL_ADMIN, 1L
        );

        Mockito.when(schoolService.entityById(1L)).thenReturn(mockSchool);
        Mockito.when(userRepository.existsByEmail("kurbanovpavlo@gmail.com")).thenReturn(false);
        Mockito.when(userRepository.existsByPhoneNumber("+380981221493")).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User(requestDTO, Role.SCHOOL_ADMIN, mockSchool));

        Mockito.when(userMapper.toUserDetailsResponseDTO(Mockito.any(User.class))).thenReturn(new UserDetailsResponseDTO(
                1L, "Pavlo", "Kurbanov", 24, "kurbanovpavlo@gmail.com", "+380981221493"
                ,  Role.SCHOOL_ADMIN, LocalDateTime.now(), LocalDateTime.now()
        ));

        UserDetailsResponseDTO userByAdmin = userService.createUserByAdmin(requestDTO);

        assertEquals(requestDTO.firstName(), userByAdmin.firstName());
        assertEquals(requestDTO.lastName(), userByAdmin.lastName());
        assertEquals(requestDTO.age(), userByAdmin.age());
        assertEquals(requestDTO.email(), userByAdmin.email());
        assertEquals(requestDTO.phoneNumber(), userByAdmin.phoneNumber());
    }

}