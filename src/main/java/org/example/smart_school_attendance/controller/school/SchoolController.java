package org.example.smart_school_attendance.controller.school;


import jakarta.validation.Valid;
import org.example.smart_school_attendance.dto.school.request.SchoolCreateDetailsDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/school")
public class SchoolController {

    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public SchoolDetailsResponseDTO create(
            @RequestBody @Valid SchoolCreateDetailsDTO createDetailsDTO) {
        return schoolService.create(createDetailsDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public SchoolDetailsResponseDTO findById(
            @PathVariable Long id) {
        return schoolService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public Page<SchoolDetailsResponseDTO> findAll(Pageable pageable) {
        return schoolService.findAll(pageable);
    }

    @GetMapping("/search/name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public SchoolDetailsResponseDTO findByName(
            @RequestParam String name) {
        return schoolService.findByName(name);
    }

    @GetMapping("/search/email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public SchoolDetailsResponseDTO findByEmail(
            @RequestParam String email) {
        return schoolService.findByEmail(email);
    }

    @GetMapping("/search/phone")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SCHOOL_ADMIN')")
    public SchoolDetailsResponseDTO findByPhone(
            @RequestParam String phone) {
        return schoolService.findByPhoneNumber(phone);
    }
}
