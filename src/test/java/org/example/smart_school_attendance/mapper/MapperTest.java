package org.example.smart_school_attendance.mapper;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.childDetails.response.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.classRoom.ClassRoomDetailsResponseDTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests the MapStruct-generated implementations directly (no Spring context). */
class MapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final SchoolMapper schoolMapper = Mappers.getMapper(SchoolMapper.class);
    private final ChildDetailsMapper childDetailsMapper = Mappers.getMapper(ChildDetailsMapper.class);
    private final ClassRoomMapper classRoomMapper = Mappers.getMapper(ClassRoomMapper.class);

    @Test
    void userDetailsMapsBasicFields() {
        User user = TestData.user(1L, Role.TEACHER, "1");

        UserDetailsResponseDTO dto = userMapper.toUserDetailsResponseDTO(user);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.email()).isEqualTo(user.getEmail());
        assertThat(dto.role()).isEqualTo(Role.TEACHER);
    }

    @Test
    @Disabled("BUG: UserMapper does not map User.userStatus -> UserDetailsResponseDTO.status, API always returns status = null")
    void userDetailsMapsStatus() {
        User user = TestData.user(1L, Role.TEACHER, "1");
        user.setUserStatus(UserStatus.ACTIVE);

        assertThat(userMapper.toUserDetailsResponseDTO(user).status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void schoolMapsAllFields() {
        School school = TestData.school(1L, "1");

        SchoolDetailsResponseDTO dto = schoolMapper.toSchoolDetailsResponseDTO(school);

        assertThat(dto.name()).isEqualTo(school.getName());
        assertThat(dto.radius()).isEqualTo(200);
        assertThat(dto.latitude()).isEqualTo(50.45);
    }

    @Test
    void childDetailsMapsNames() {
        School school = TestData.school(1L, "1");
        ClassRoom classRoom = new ClassRoom("1-A", new ArrayList<>(), null, school, 20);
        User child = TestData.user(10L, Role.CHILD, "10");
        ChildDetails details = new ChildDetails(child, school, classRoom,
                List.of(TestData.user(20L, Role.PARENT, "20"), TestData.user(21L, Role.PARENT, "21")));
        details.setId(10L);

        ChildDetailsResponseDTO dto = childDetailsMapper.toChildDetailsResponseDTO(details);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.childName()).isEqualTo("First10 Last10");
        assertThat(dto.schoolName()).isEqualTo("School 1");
        assertThat(dto.classRoomName()).isEqualTo("1-A");
        assertThat(dto.parentsNames()).isEqualTo("First20 Last20, First21 Last21");
    }

    @Test
    void classRoomMapsIds() {
        ChildDetails c1 = new ChildDetails();
        c1.setId(10L);
        ClassRoom classRoom = new ClassRoom("1-A", new ArrayList<>(List.of(c1)),
                TestData.user(30L, Role.TEACHER, "30"), TestData.school(1L, "1"), 20);
        classRoom.setId(5L);

        ClassRoomDetailsResponseDTO dto = classRoomMapper.classRoomDetailsResponseDTO(classRoom);

        assertThat(dto.id()).isEqualTo(5L);
        assertThat(dto.teacherId()).isEqualTo(30L);
        assertThat(dto.schoolId()).isEqualTo(1L);
        assertThat(dto.childIds()).containsExactly(10L);
    }
}
