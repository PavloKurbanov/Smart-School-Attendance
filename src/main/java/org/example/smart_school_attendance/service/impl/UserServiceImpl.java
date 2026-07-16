package org.example.smart_school_attendance.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.UserMapper;
import org.example.smart_school_attendance.dto.user.request.CreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.response.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional(readOnly = true)
    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID can't be null or < 0");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserListResponseDTO> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable can't be null");
        }
        return userRepository.findAll(pageable)
                .map(userMapper::toUserListResponseDTO);
    }

    @Override
    @Transactional
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("User ID can't be null");
        }

        log.debug("Attempting to update user with ID: {}", user.getId());
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User with ID: " + user.getId() + " not found");
        }
        userRepository.save(user);
        log.info("User with ID: {} successfully updated", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email can't be null or empty");
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number can't be null or empty");
        }
        return userRepository.existByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsResponseDTO findByEntityId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID can't be null");
        }
        log.debug("Attempting to find user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + id + " not found"));
        return userMapper.toUserDetailsResponseDTO(user);
    }
}
