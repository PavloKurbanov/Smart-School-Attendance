package org.example.smart_school_attendance.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.UserMapper;
import org.example.smart_school_attendance.dto.CreateUserRequestDTO;
import org.example.smart_school_attendance.dto.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetailsResponseDTO create(CreateUserRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }
        log.debug("Attempting to create new user with email: {}, phone number: {}", request.email(), request.phoneNumber());

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email " + request.email() + " already exists");
        }

        if (userRepository.existByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number " + request.phoneNumber() + " already exists");
        }

        String encode = passwordEncoder.encode(request.password());

        User user = userRepository.save(new User(
                request.firstName(),
                request.lastName(),
                request.age(),
                request.email(),
                encode,
                request.phoneNumber(),
                request.role()
        ));
        log.info("User with email: {}, phone number: {} successfully created with ID: {}", request.email(), request.phoneNumber(), user.getId());
        return userMapper.toUserDetailsResponseDTO(user);
    }

    @Override
    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID can't be null or < 0");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + id + " not found"));
    }

    @Override
    public Page<UserListResponseDTO> findAll(Pageable pageable) {
        return null;
    }

    private UserDetailsResponseDTO mapToDetails(User user) {
        return new UserDetailsResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreateDate(),
                user.getUpdateDate()
        );
    }
}
