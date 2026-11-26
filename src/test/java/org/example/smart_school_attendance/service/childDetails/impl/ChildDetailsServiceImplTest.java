package org.example.smart_school_attendance.service.childDetails.impl;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.childDetails.request.CreateChildDetailsRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.childDetails.ChildLocationStatus;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.ChildDetailsMapper;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.service.classRoom.ClassRoomService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChildDetailsServiceImplTest {

    @Mock
    private ChildDetailsRepository childDetailsRepository;
    @Mock
    private ChildDetailsMapper childDetailsMapper;
    @Mock
    private UserService userService;
    @Mock
    private SchoolService schoolService;
    @Mock
    private ClassRoomService classRoomService;

    private ChildDetailsServiceImpl childDetailsService;

    private User child;
    private User parent;
    private School school;
    private ClassRoom classRoom;

    @BeforeEach
    void setUp() {
        childDetailsService = new ChildDetailsServiceImpl(childDetailsRepository, childDetailsMapper,
                userService, schoolService, classRoomService);
        child = TestData.user(10L, Role.CHILD, "10");
        parent = TestData.user(20L, Role.PARENT, "20");
        school = TestData.school(1L, "1");
        classRoom = new ClassRoom("1-A", new ArrayList<>(), TestData.user(30L, Role.TEACHER, "30"), school, 20);
        classRoom.setId(5L);
    }

    @Test
    void createsChildDetailsWithLoadedEntities() {
        when(userService.entityById(10L)).thenReturn(child);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(classRoomService.entityById(5L)).thenReturn(classRoom);
        when(userService.findAllByIds(List.of(20L))).thenReturn(List.of(parent));

        childDetailsService.createChildDetails(new CreateChildDetailsRequestDTO(10L, 1L, 5L, List.of(20L)));

        ArgumentCaptor<ChildDetails> saved = ArgumentCaptor.forClass(ChildDetails.class);
        verify(childDetailsRepository).save(saved.capture());
        assertThat(saved.getValue().getChild()).isSameAs(child);
        assertThat(saved.getValue().getSchool()).isSameAs(school);
        assertThat(saved.getValue().getClassRoom()).isSameAs(classRoom);
        assertThat(saved.getValue().getParents()).containsExactly(parent);
        assertThat(saved.getValue().getStatus()).isEqualTo(ChildLocationStatus.INSIDE_SCHOOL);
    }

    @Test
    void nullRequestIsRejected() {
        assertThatThrownBy(() -> childDetailsService.createChildDetails(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAllByIdWithEmptyListReturnsEmpty() {
        assertThat(childDetailsService.findAllById(List.of())).isEmpty();
        assertThat(childDetailsService.findAllById(null)).isEmpty();
    }

    @Test
    @Disabled("BUG: any user can be registered as a child; role CHILD is not checked")
    void userWhoIsNotChildIsRejected() {
        User teacher = TestData.user(10L, Role.TEACHER, "10");
        when(userService.entityById(10L)).thenReturn(teacher);

        assertThatThrownBy(() -> childDetailsService.createChildDetails(new CreateChildDetailsRequestDTO(10L, 1L, 5L, List.of(20L))))
                .isInstanceOf(IllegalArgumentException.class);
        verify(childDetailsRepository, never()).save(any());
    }

    @Test
    @Disabled("BUG: parents are not checked for role PARENT")
    void parentWhoIsNotParentIsRejected() {
        when(userService.entityById(10L)).thenReturn(child);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(classRoomService.entityById(5L)).thenReturn(classRoom);
        when(userService.findAllByIds(List.of(30L))).thenReturn(List.of(TestData.user(30L, Role.TEACHER, "30")));

        assertThatThrownBy(() -> childDetailsService.createChildDetails(new CreateChildDetailsRequestDTO(10L, 1L, 5L, List.of(30L))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: unknown parent ids are silently ignored (findAllById returns fewer users)")
    void unknownParentIdIsRejected() {
        when(userService.entityById(10L)).thenReturn(child);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(classRoomService.entityById(5L)).thenReturn(classRoom);
        when(userService.findAllByIds(List.of(20L, 999L))).thenReturn(List.of(parent));

        assertThatThrownBy(() -> childDetailsService.createChildDetails(new CreateChildDetailsRequestDTO(10L, 1L, 5L, List.of(20L, 999L))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: class room of another school is accepted")
    void classRoomFromAnotherSchoolIsRejected() {
        School otherSchool = TestData.school(2L, "2");
        when(userService.entityById(10L)).thenReturn(child);
        when(schoolService.entityById(2L)).thenReturn(otherSchool);
        when(classRoomService.entityById(5L)).thenReturn(classRoom);
        when(userService.findAllByIds(List.of(20L))).thenReturn(List.of(parent));

        assertThatThrownBy(() -> childDetailsService.createChildDetails(new CreateChildDetailsRequestDTO(10L, 2L, 5L, List.of(20L))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: class room capacity is not checked when adding a child")
    void fullClassRoomIsRejected() {
        classRoom.setCapacity(10);
        when(childDetailsRepository.countByClassRoomId(5L)).thenReturn(10L);
        when(userService.entityById(10L)).thenReturn(child);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(classRoomService.entityById(5L)).thenReturn(classRoom);
        when(userService.findAllByIds(List.of(20L))).thenReturn(List.of(parent));

        assertThatThrownBy(() -> childDetailsService.createChildDetails(new CreateChildDetailsRequestDTO(10L, 1L, 5L, List.of(20L))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
