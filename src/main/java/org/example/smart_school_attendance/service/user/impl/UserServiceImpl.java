package org.example.smart_school_attendance.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.security.UserAuthDto;
import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserRegistrationRequest;
import org.example.smart_school_attendance.dto.user.request.UserUpdateDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.response.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.UserMapper;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.service.user.UserService;
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
    public UserDetailsResponseDTO create(PublicRegisterRequestDTO request) {
        User userInternal = createUserInternal(request, Role.PARENT);
        return userMapper.toUserDetailsResponseDTO(userInternal);
    }

    @Override
    @Transactional
    public UserDetailsResponseDTO createUserByAdmin(AdminCreateUserRequestDTO request) {
        User userInternal = createUserInternal(request, request.role());
        return userMapper.toUserDetailsResponseDTO(userInternal);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsResponseDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID can't be null or < 0");
        }
        return userRepository.findById(id).map(userMapper::toUserDetailsResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + id + " not found"));
    }

    @Override
    public UserAuthDto authenticate(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email can't be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password can't be null or blank");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email: " + email + " not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return new UserAuthDto(user.getId(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public User entityById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID can't be null or less than 0");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsResponseDTO findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email can't be null or blank");
        }
        return userRepository.findByEmail(email).map(userMapper::toUserDetailsResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("User with email: " + email + " not found"));
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
    public void update(UserUpdateDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        log.debug("Attempting to update user with ID: {}", request.id());

        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("User with ID: " + request.id() + " not found"));

        if (request.email() != null && !request.email().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(request.email(), user.getId())) {
                throw new IllegalArgumentException("User with email: " + request.email() + " already exists");
            }
            user.setEmail(request.email());
        }

        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumberAndIdNot(request.phoneNumber(), user.getId())) {
                throw new IllegalArgumentException("User with phone number: " + request.phoneNumber() + " already exists");
            }
            user.setPhoneNumber(request.phoneNumber());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(request.password());
        }
        log.info("User with ID: {} successfully updated in database", request.id());
        userRepository.save(user);
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
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    private User createUserInternal(UserRegistrationRequest userDTO, Role role) {
        if (userDTO == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }

        if (role == null) {
            throw new IllegalArgumentException("Create user role can't be null");
        }

        log.debug("Attempting to create new user with email: {}, phone number: {}, role: {}", userDTO.email(), userDTO.phoneNumber(), role);

        if (userRepository.existsByEmail(userDTO.email())) {
            throw new IllegalArgumentException("Email " + userDTO.email() + " already exists");
        }

        if (userRepository.existsByPhoneNumber(userDTO.phoneNumber())) {
            throw new IllegalArgumentException("Phone number " + userDTO.phoneNumber() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.password());
        User user = new User(userDTO, role);
        user.setPassword(encodedPassword);

        user = userRepository.save(user);

        log.info("User with email: {}, phone number: {}, role: {} successfully created with ID: {}"
                , user.getEmail(), user.getPhoneNumber(), user.getRole(), user.getId());
        return user;
    }
}
