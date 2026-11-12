package org.example.smart_school_attendance.service.school.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.school.request.SchoolCreateRequestTO;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateRequestDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.mapper.SchoolMapper;
import org.example.smart_school_attendance.repository.SchoolRepository;
import org.example.smart_school_attendance.security.SecurityUser;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;
    private final SecurityService securityService;

    @Autowired
    public SchoolServiceImpl(SchoolRepository schoolRepository, SchoolMapper schoolMapper, SecurityService securityService) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public SchoolDetailsResponseDTO create(SchoolCreateRequestTO responseDTO) {
        if (responseDTO == null) {
            throw new IllegalArgumentException("Create school request can't be null");
        }
        log.debug("Attempting to create new school with name: {}, email: {}, phone number: {}"
                , responseDTO.name(), responseDTO.email(), responseDTO.phoneNumber());

        if (schoolRepository.existsByEmail(responseDTO.email())) {
            throw new IllegalArgumentException("Email " + responseDTO.email() + " already exists");
        }

        if (schoolRepository.existsByAddress(responseDTO.address())) {
            throw new IllegalArgumentException("Address " + responseDTO.address() + " already exists");
        }

        if (schoolRepository.existsByPhoneNumber(responseDTO.phoneNumber())) {
            throw new IllegalArgumentException("Phone number " + responseDTO.phoneNumber() + " already exists");
        }

        School school = schoolRepository.save(new School(
                responseDTO.name(),
                responseDTO.address(),
                responseDTO.phoneNumber(),
                responseDTO.email(),
                responseDTO.latitude(),
                responseDTO.longitude(),
                responseDTO.radius()
        ));
        log.info("School with name: {}, email: {}, phone number: {}, address: {} successfully created with ID: {}"
                , responseDTO.name(), responseDTO.email(), responseDTO.phoneNumber(), responseDTO.address(), school.getId());
        return schoolMapper.toSchoolDetailsResponseDTO(school);
    }

    @Override
    @Transactional
    public void update(Long schoolId, SchoolUpdateRequestDTO schoolUpdateRequestDTO) {
        if(schoolId == null || schoolId <= 0) {
            throw new IllegalArgumentException("School ID can't be null or less than 0");
        }

        if (schoolUpdateRequestDTO == null) {
            throw new IllegalArgumentException("Update school request can't be null");
        }

        log.debug("Attempting to update school with ID: {}", schoolId);

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School ID: " + schoolId + " not found"));

        if (schoolUpdateRequestDTO.email() != null && !schoolUpdateRequestDTO.email().isBlank()) {
            if (schoolRepository.existsByEmailAndIdNot(schoolUpdateRequestDTO.email(), school.getId())) {
                throw new IllegalArgumentException("School with email: " + schoolUpdateRequestDTO.email() + " already exists");
            }
            school.setEmail(schoolUpdateRequestDTO.email());
            log.info("School with ID: {} successfully updated email address to: {}", schoolId, schoolUpdateRequestDTO.email());
        }

        if (schoolUpdateRequestDTO.phoneNumber() != null && !schoolUpdateRequestDTO.phoneNumber().isBlank()) {
            if (schoolRepository.existsByPhoneNumberAndIdNot(schoolUpdateRequestDTO.phoneNumber(), school.getId())) {
                throw new IllegalArgumentException("School with phone number: " + schoolUpdateRequestDTO.phoneNumber() + " already exists");
            }
            school.setPhoneNumber(schoolUpdateRequestDTO.phoneNumber());
            log.info("School with ID: {} successfully updated phone number to: {}", schoolId, schoolUpdateRequestDTO.phoneNumber());
        }
        schoolRepository.save(school);
        log.info("School with ID: {} successfully updated", schoolId);
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolDetailsResponseDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("School ID can't be null or empty");
        }
        return schoolRepository.findById(id)
                .map(schoolMapper::toSchoolDetailsResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("School with ID: " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolDetailsResponseDTO findByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("School name can't be null or empty");
        }
        return schoolRepository.findByName(name)
                .map(schoolMapper::toSchoolDetailsResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("School with name: " + name + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolDetailsResponseDTO findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("School email can't be null or empty");
        }
        return schoolRepository.findByEmail(email).map(schoolMapper::toSchoolDetailsResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("School with email: " + email + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolDetailsResponseDTO findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number can't be null or empty");
        }
        return schoolRepository.findByPhoneNumber(phoneNumber).map(schoolMapper::toSchoolDetailsResponseDTO).
                orElseThrow(() -> new IllegalArgumentException("School with phone number: " + phoneNumber + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SchoolDetailsResponseDTO> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable can't be null");
        }
        return schoolRepository.findAll(pageable)
                .map(schoolMapper::toSchoolDetailsResponseDTO);
    }

    @Override
    public School entityById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("School with ID: " + id + " not found"));
    }
}
