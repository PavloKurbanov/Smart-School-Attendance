package org.example.smart_school_attendance.integration;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.childDetails.request.CreateChildDetailsRequestDTO;
import org.example.smart_school_attendance.dto.childDetails.response.ChildDetailsResponseDTO;
import org.example.smart_school_attendance.dto.classRoom.ClassRoomDetailsResponseDTO;
import org.example.smart_school_attendance.dto.classRoom.CreateClassRoomRequestDTO;
import org.example.smart_school_attendance.dto.login.LoginRequestDTO;
import org.example.smart_school_attendance.dto.school.request.SchoolCreateRequestTO;
import org.example.smart_school_attendance.dto.school.response.SchoolDetailsResponseDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.example.smart_school_attendance.repository.ChildDetailsRepository;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.security.jwt.JwtService;
import org.example.smart_school_attendance.service.auth.AuthService;
import org.example.smart_school_attendance.service.childDetails.ChildDetailsService;
import org.example.smart_school_attendance.service.classRoom.ClassRoomService;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.example.smart_school_attendance.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Real services + JPA on H2. Each test runs in a transaction that is rolled back. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceFlowIntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private ChildDetailsService childDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChildDetailsRepository childDetailsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EntityManager em;

    private SchoolDetailsResponseDTO school;

    @BeforeEach
    void setUp() {
        school = schoolService.create(new SchoolCreateRequestTO("School 1", "Address 1", "+380441111111",
                "school1@test.com", 50.45, 30.52, 300));
    }

    private User createUser(Role role, String suffix) {
        UserDetailsResponseDTO dto = userService.createUserByAdmin(TestData.adminCreateRequest(suffix, role, school.id()));
        User user = userRepository.findById(dto.id()).orElseThrow();
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    @Test
    void registrationThenLogin() {
        PublicRegisterRequestDTO request = TestData.registerRequest("1", school.id());

        String registrationToken = authService.registration(request);

        User saved = userRepository.findByEmail(request.email()).orElseThrow();
        assertThat(saved.getRole()).isEqualTo(Role.PARENT);
        assertThat(saved.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(saved.getSchool().getId()).isEqualTo(school.id());
        assertThat(passwordEncoder.matches(request.password(), saved.getPassword())).isTrue();
        assertThat(jwtService.isUserActivated(registrationToken)).isFalse();

        userService.updateStatus(saved.getId(), UserStatus.ACTIVE);
        String loginToken = authService.login(new LoginRequestDTO(request.email(), request.password()));

        assertThat(jwtService.isUserActivated(loginToken)).isTrue();
        assertThat(jwtService.extractClaim(loginToken, "userId")).isEqualTo(saved.getId().toString());
    }

    @Test
    void registrationWithTakenEmailIsRejected() {
        authService.registration(TestData.registerRequest("1", school.id()));
        PublicRegisterRequestDTO sameEmail = new PublicRegisterRequestDTO("X", "Y", 30, "user1@test.com",
                TestData.phone("2"), "pwd", school.id());

        assertThatThrownBy(() -> authService.registration(sameEmail)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registrationForUnknownSchoolIsRejected() {
        assertThatThrownBy(() -> authService.registration(TestData.registerRequest("1", 999_999L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: schoolId is optional in AdminCreateUserRequestDTO, but SchoolServiceImpl.entityById(null) "
            + "throws InvalidDataAccessApiUsageException (HTTP 500)")
    void adminCanCreateUserWithoutSchool() {
        assertThatCode(() -> userService.createUserByAdmin(TestData.adminCreateRequest("9", Role.SYSTEM_ADMIN, null)))
                .doesNotThrowAnyException();
    }

    @Test
    void schoolWithDuplicateEmailIsRejected() {
        SchoolCreateRequestTO duplicate = new SchoolCreateRequestTO("Other", "Other address", "+380442222222",
                "school1@test.com", 50.0, 30.0, 100);

        assertThatThrownBy(() -> schoolService.create(duplicate)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: school name is not unique, but findByName returns Optional -> IncorrectResultSizeDataAccessException (HTTP 500)")
    void searchByNameWorksWhenTwoSchoolsShareName() {
        schoolService.create(new SchoolCreateRequestTO("School 1", "Address 2", "+380442222222",
                "school2@test.com", 50.0, 30.0, 100));

        assertThatCode(() -> schoolService.findByName("School 1")).doesNotThrowAnyException();
    }

    @Test
    void classRoomAndChildDetailsFlow() {
        User teacher = createUser(Role.TEACHER, "30");
        User child = createUser(Role.CHILD, "10");
        User parent = createUser(Role.PARENT, "20");

        ClassRoomDetailsResponseDTO classRoom = classRoomService.createClassRoom(
                new CreateClassRoomRequestDTO("1-A", null, teacher.getId(), school.id(), 20));
        ChildDetailsResponseDTO childDetails = childDetailsService.createChildDetails(
                new CreateChildDetailsRequestDTO(child.getId(), school.id(), classRoom.id(), List.of(parent.getId())));

        assertThat(classRoom.teacherId()).isEqualTo(teacher.getId());
        assertThat(classRoom.schoolId()).isEqualTo(school.id());
        assertThat(childDetails.id()).isEqualTo(child.getId());
        assertThat(childDetails.childName()).isEqualTo("First10 Last10");
        assertThat(childDetails.schoolName()).isEqualTo("School 1");
        assertThat(childDetails.classRoomName()).isEqualTo("1-A");
        assertThat(childDetails.parentsNames()).isEqualTo("First20 Last20");

        em.flush();
        em.clear();
        assertThat(childDetailsRepository.countByClassRoomId(classRoom.id())).isEqualTo(1);
        assertThat(classRoomService.getClassRoomById(classRoom.id()).childIds()).containsExactly(child.getId());
    }

    @Test
    void existingChildrenAreMovedToNewClassRoom() {
        User teacher1 = createUser(Role.TEACHER, "30");
        User teacher2 = createUser(Role.TEACHER, "31");
        User child = createUser(Role.CHILD, "10");
        User parent = createUser(Role.PARENT, "20");
        ClassRoomDetailsResponseDTO first = classRoomService.createClassRoom(
                new CreateClassRoomRequestDTO("1-A", null, teacher1.getId(), school.id(), 20));
        childDetailsService.createChildDetails(
                new CreateChildDetailsRequestDTO(child.getId(), school.id(), first.id(), List.of(parent.getId())));

        ClassRoomDetailsResponseDTO second = classRoomService.createClassRoom(
                new CreateClassRoomRequestDTO("1-B", List.of(child.getId()), teacher2.getId(), school.id(), 20));
        em.flush();
        em.clear();

        ChildDetails reloaded = childDetailsRepository.findById(child.getId()).orElseThrow();
        assertThat(reloaded.getClassRoom().getId()).isEqualTo(second.id());
    }

    @Test
    @Disabled("BUG: parentIds is optional in CreateChildDetailsRequestDTO, but UserService.findAllByIds throws on null/empty list")
    void childWithoutParentsCanBeCreated() {
        User teacher = createUser(Role.TEACHER, "30");
        User child = createUser(Role.CHILD, "10");
        ClassRoomDetailsResponseDTO classRoom = classRoomService.createClassRoom(
                new CreateClassRoomRequestDTO("1-A", null, teacher.getId(), school.id(), 20));

        assertThatCode(() -> childDetailsService.createChildDetails(
                new CreateChildDetailsRequestDTO(child.getId(), school.id(), classRoom.id(), null)))
                .doesNotThrowAnyException();
    }

    @Test
    @Disabled("BUG: ClassRoom.teacher is @OneToOne (unique teacher_id) but the service does not check it -> "
            + "DataIntegrityViolationException (HTTP 500) instead of a clear 400")
    void teacherWithClassRoomCannotGetSecondOneWithClearError() {
        User teacher = createUser(Role.TEACHER, "30");
        classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-A", null, teacher.getId(), school.id(), 20));

        assertThatThrownBy(() -> {
            classRoomService.createClassRoom(new CreateClassRoomRequestDTO("1-B", null, teacher.getId(), school.id(), 20));
            em.flush();
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Disabled("BUG: creating child details twice for the same child is not checked -> DuplicateKeyException (HTTP 500)")
    void childDetailsCannotBeCreatedTwice() {
        User teacher = createUser(Role.TEACHER, "30");
        User child = createUser(Role.CHILD, "10");
        User parent = createUser(Role.PARENT, "20");
        ClassRoomDetailsResponseDTO classRoom = classRoomService.createClassRoom(
                new CreateClassRoomRequestDTO("1-A", null, teacher.getId(), school.id(), 20));
        CreateChildDetailsRequestDTO request =
                new CreateChildDetailsRequestDTO(child.getId(), school.id(), classRoom.id(), List.of(parent.getId()));
        childDetailsService.createChildDetails(request);
        em.flush();

        assertThatThrownBy(() -> {
            childDetailsService.createChildDetails(request);
            em.flush();
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
