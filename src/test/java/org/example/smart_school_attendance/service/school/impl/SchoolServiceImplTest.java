package org.example.smart_school_attendance.service.school.impl;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.school.request.SchoolCreateRequestTO;
import org.example.smart_school_attendance.dto.school.request.SchoolUpdateRequestDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.mapper.SchoolMapper;
import org.example.smart_school_attendance.repository.SchoolRepository;
import org.example.smart_school_attendance.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolServiceImplTest {

    @Mock
    private SchoolRepository schoolRepository;
    @Mock
    private SchoolMapper schoolMapper;
    @Mock
    private SecurityService securityService;

    private SchoolServiceImpl schoolService;

    @BeforeEach
    void setUp() {
        schoolService = new SchoolServiceImpl(schoolRepository, schoolMapper, securityService);
    }

    private static SchoolCreateRequestTO createRequest() {
        return new SchoolCreateRequestTO("School 1", "Address 1", "+380441234567", "school@test.com", 50.45, 30.52, 300);
    }

    @Nested
    class Create {

        @Test
        void savesSchoolWithRequestData() {
            when(schoolRepository.save(any(School.class))).thenAnswer(inv -> inv.getArgument(0));

            schoolService.create(createRequest());

            ArgumentCaptor<School> saved = ArgumentCaptor.forClass(School.class);
            verify(schoolRepository).save(saved.capture());
            assertThat(saved.getValue())
                    .extracting(School::getName, School::getAddress, School::getPhoneNumber, School::getEmail,
                            School::getLatitude, School::getLongitude, School::getRadius)
                    .containsExactly("School 1", "Address 1", "+380441234567", "school@test.com", 50.45, 30.52, 300);
        }

        @Test
        void duplicateEmailIsRejected() {
            when(schoolRepository.existsByEmail("school@test.com")).thenReturn(true);

            assertThatThrownBy(() -> schoolService.create(createRequest())).isInstanceOf(IllegalArgumentException.class);
            verify(schoolRepository, never()).save(any());
        }

        @Test
        void duplicateAddressIsRejected() {
            when(schoolRepository.existsByAddress("Address 1")).thenReturn(true);

            assertThatThrownBy(() -> schoolService.create(createRequest())).isInstanceOf(IllegalArgumentException.class);
            verify(schoolRepository, never()).save(any());
        }

        @Test
        void duplicatePhoneIsRejected() {
            when(schoolRepository.existsByPhoneNumber("+380441234567")).thenReturn(true);

            assertThatThrownBy(() -> schoolService.create(createRequest())).isInstanceOf(IllegalArgumentException.class);
            verify(schoolRepository, never()).save(any());
        }

        @Test
        void nullRequestIsRejected() {
            assertThatThrownBy(() -> schoolService.create(null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void updatesEmailAndPhone() {
            School school = TestData.school(1L, "1");
            when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

            schoolService.update(1L, SchoolUpdateRequestDTO.builder().email("new@test.com").phoneNumber("+380449999999").build());

            assertThat(school.getEmail()).isEqualTo("new@test.com");
            assertThat(school.getPhoneNumber()).isEqualTo("+380449999999");
            verify(schoolRepository).save(school);
        }

        @Test
        void emailTakenByAnotherSchoolIsRejected() {
            School school = TestData.school(1L, "1");
            when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
            when(schoolRepository.existsByEmailAndIdNot("taken@test.com", 1L)).thenReturn(true);

            assertThatThrownBy(() -> schoolService.update(1L, SchoolUpdateRequestDTO.builder().email("taken@test.com").build()))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(schoolRepository, never()).save(any());
        }

        @Test
        void unknownSchoolIsRejected() {
            when(schoolRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolService.update(1L, SchoolUpdateRequestDTO.builder().email("x@test.com").build()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Lookups {

        @Test
        void invalidIdIsRejected() {
            assertThatThrownBy(() -> schoolService.findById(0L)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> schoolService.findById(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void blankSearchValuesAreRejected() {
            assertThatThrownBy(() -> schoolService.findByName("")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> schoolService.findByEmail("")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> schoolService.findByPhoneNumber(" ")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void missingSchoolIsReportedAsNotFound() {
            when(schoolRepository.findByName("Nope")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolService.findByName("Nope"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        void entityByIdOfMissingSchoolIsRejected() {
            when(schoolRepository.findById(7L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolService.entityById(7L)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
