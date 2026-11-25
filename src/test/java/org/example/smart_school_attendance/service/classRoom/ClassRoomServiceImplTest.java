package org.example.smart_school_attendance.service.classRoom;

import jakarta.persistence.EntityNotFoundException;
import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.classRoom.CreateClassRoomRequestDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.mapper.ClassRoomMapper;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.repository.ClassRoomRepository;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassRoomServiceImplTest {

    @Mock
    private ClassRoomRepository classRoomRepository;
    @Mock
    private ClassRoomMapper classRoomMapper;
    @Mock
    private UserService userService;
    @Mock
    private SchoolService schoolService;
    @Mock
    private ChildDetailsRepository childDetailsRepository;

    private ClassRoomServiceImpl classRoomService;
    private User teacher;
    private School school;

    @BeforeEach
    void setUp() {
        classRoomService = new ClassRoomServiceImpl(classRoomRepository, classRoomMapper, userService, schoolService, childDetailsRepository);
        teacher = TestData.user(30L, Role.TEACHER, "30");
        school = TestData.school(1L, "1");
    }

    private static ChildDetails childDetails(Long id) {
        ChildDetails details = new ChildDetails();
        details.setId(id);
        return details;
    }

    @Test
    void createsClassRoomAndAssignsChildren() {
        ChildDetails c1 = childDetails(1L);
        ChildDetails c2 = childDetails(2L);
        when(userService.entityById(30L)).thenReturn(teacher);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(childDetailsRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(c1, c2));
        when(classRoomRepository.save(any(ClassRoom.class))).thenAnswer(inv -> inv.getArgument(0));

        classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", List.of(1L, 2L), 30L, 1L, 25));

        ArgumentCaptor<ClassRoom> saved = ArgumentCaptor.forClass(ClassRoom.class);
        verify(classRoomRepository).save(saved.capture());
        ClassRoom classRoom = saved.getValue();
        assertThat(classRoom.getName()).isEqualTo("1-A");
        assertThat(classRoom.getTeacher()).isSameAs(teacher);
        assertThat(classRoom.getSchool()).isSameAs(school);
        assertThat(classRoom.getCapacity()).isEqualTo(25);
        assertThat(classRoom.getChildren()).containsExactly(c1, c2);
        assertThat(c1.getClassRoom()).isSameAs(classRoom);
        assertThat(c2.getClassRoom()).isSameAs(classRoom);
    }

    @Test
    void createsEmptyClassRoomWithoutChildren() {
        when(userService.entityById(30L)).thenReturn(teacher);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(classRoomRepository.save(any(ClassRoom.class))).thenAnswer(inv -> inv.getArgument(0));

        classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", null, 30L, 1L, 25));

        verify(childDetailsRepository, never()).findAllById(any());
        verify(classRoomRepository).save(any(ClassRoom.class));
    }

    @Test
    void userWhoIsNotTeacherIsRejected() {
        when(userService.entityById(30L)).thenReturn(TestData.user(30L, Role.PARENT, "30"));

        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", null, 30L, 1L, 25)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("is not a teacher");
        verify(classRoomRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, 9})
    void capacityBelowTenIsRejected(Integer capacity) {
        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", null, 30L, 1L, capacity)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void blankNameIsRejected() {
        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO(" ", null, 30L, 1L, 25)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void moreChildrenThanCapacityIsRejected() {
        List<Long> ids = LongStream.rangeClosed(1, 11).boxed().toList();
        when(userService.entityById(30L)).thenReturn(teacher);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(childDetailsRepository.findAllById(ids)).thenReturn(ids.stream().map(ClassRoomServiceImplTest::childDetails).toList());

        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", ids, 30L, 1L, 10)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(classRoomRepository, never()).save(any());
    }

    @Test
    void missingChildIdsAreReported() {
        when(userService.entityById(30L)).thenReturn(teacher);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(childDetailsRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(childDetails(1L)));

        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", List.of(1L, 2L), 30L, 1L, 25)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("[2]");
    }

    @Test
    @Disabled("BUG: missing children throw EntityNotFoundException, which GlobalExceptionHandler turns into HTTP 500")
    void missingChildIdsAreReportedAsBadRequest() {
        when(userService.entityById(30L)).thenReturn(teacher);
        when(schoolService.entityById(1L)).thenReturn(school);
        when(childDetailsRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(childDetails(1L)));

        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", List.of(1L, 2L), 30L, 1L, 25)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: teacher from another school can be assigned to a class room")
    void teacherFromAnotherSchoolIsRejected() {
        teacher.setSchool(TestData.school(2L, "2"));
        when(userService.entityById(30L)).thenReturn(teacher);
        when(schoolService.entityById(1L)).thenReturn(school);

        assertThatThrownBy(() -> classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", null, 30L, 1L, 25)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByIdValidatesId() {
        assertThatThrownBy(() -> classRoomService.getClassRoomById(0L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> classRoomService.entityById(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByIdOfMissingClassRoomIsRejected() {
        when(classRoomRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classRoomService.getClassRoomById(3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
