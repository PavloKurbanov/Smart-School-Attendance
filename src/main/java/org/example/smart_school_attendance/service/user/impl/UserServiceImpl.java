package org.example.smart_school_attendance.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.response.UserListResponseDTO;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.UserMapper;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (request == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }
        log.debug("Attempting to create new user with email: {}, phone number: {}", request.email(), request.phoneNumber());

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email " + request.email() + " already exists");
        }

        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number " + request.phoneNumber() + " already exists");
        }

        String encode = passwordEncoder.encode(request.password());

        User user = userRepository.save(new User(
                request.firstName(),
                request.lastName(),
                request.age(),
                request.email(),
                request.phoneNumber(),
                encode,
                Role.PARENT
        ));
        log.info("User with email: {}, phone number: {}, role: {} successfully created with ID: {}"
                , request.email(), request.phoneNumber(), user.getRole(), user.getId());
        return userMapper.toUserDetailsResponseDTO(user);
    }

    @Override
    @Transactional
    public UserDetailsResponseDTO createUserByAdmin(AdminCreateUserRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Create user request can't be null");
        }
        log.debug("Attempting to create new user with email: {}, phone number: {}", request.email(), request.phoneNumber());

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email " + request.email() + " already exists");
        }

        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number " + request.phoneNumber() + " already exists");
        }

        String encode = passwordEncoder.encode(request.password());

        User user = userRepository.save(new User(
                request.firstName(),
                request.lastName(),
                request.age(),
                request.email(),
                request.phoneNumber(),
                encode,
                request.role()
        ));
        log.info("User with email: {}, phone number: {}, role: {} successfully created with ID: {}"
                , request.email(), request.phoneNumber(), user.getRole(), user.getId());
        return userMapper.toUserDetailsResponseDTO(user);
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
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    private UserDetailsResponseDTO createUserInternal(
            String firstName, String lastName, int age,
            String email, String phoneNumber, String rawPassword, Role role
    ) {
        log.debug("Attempting to create new user with email: {}, phone number: {}, role: {}", email, phoneNumber, role);

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(firstName, lastName, age, email, phoneNumber, encodedPassword, role);

        try {
            user = userRepository.save(user);
            // Тут треба добре подумати, так як, в мене не налаштовані unique в entity
        } catch (DataIntegrityViolationException ex) {
            log.warn("Failed to create user due to duplicate email or phone: {}", email);
            throw new IllegalArgumentException("User with such email or phone number already exists.");
        }
        log.info("User with email: {}, phone number: {}, role: {} successfully created with ID: {}"
                , user.getEmail(), user.getPhoneNumber(), user.getRole(), user.getId());
        return userMapper.toUserDetailsResponseDTO(user);
    }
}
