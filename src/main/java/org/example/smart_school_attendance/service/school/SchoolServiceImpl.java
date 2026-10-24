package org.example.smart_school_attendance.service.school;

import lombok.extern.slf4j.Slf4j;
import org.example.smart_school_attendance.dto.school.request.SchoolCreateDetailsDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.mapper.SchoolMapper;
import org.example.smart_school_attendance.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    @Autowired
    public SchoolServiceImpl(SchoolRepository schoolRepository, SchoolMapper schoolMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
    }

    @Override
    public School create(SchoolCreateDetailsDTO responseDTO) {
        return null;
    }

    @Override
    public School update(School school) {
        return null;
    }

    @Override
    public SchoolDetailsResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public SchoolDetailsResponseDTO findByName(String name) {
        return null;
    }

    @Override
    public SchoolDetailsResponseDTO findByEmail(String email) {
        return null;
    }

    @Override
    public SchoolDetailsResponseDTO findByPhoneNumber(String phoneNumber) {
        return null;
    }
}
