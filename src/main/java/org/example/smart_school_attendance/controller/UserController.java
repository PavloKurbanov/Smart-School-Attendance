package org.example.smart_school_attendance.controller;

import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.response.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.UserMapper;
import org.example.smart_school_attendance.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public UserDetailsResponseDTO create(
            @Valid @RequestBody PublicRegisterRequestDTO publicRegisterRequestDTO) {
        return userService.create(publicRegisterRequestDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public UserDetailsResponseDTO findById(
            @PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public Page<UserListResponseDTO> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }
}
