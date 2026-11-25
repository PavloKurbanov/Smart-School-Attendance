package org.example.smart_school_attendance.repository;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** JPA mappings and derived queries against H2 (MySQL mode, see application-test.properties). */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private ClassRoomRepository classRoomRepository;
    @Autowired
    private ChildDetailsRepository childDetailsRepository;

    private School school(String suffix) {
        return em.persist(TestData.school(null, suffix));
    }

    private User user(Role role, String suffix, School school) {
        User user = TestData.user(null, role, suffix);
        user.setSchool(school);
        return em.persist(user);
    }

    @Test
    void userGetsIdTimestampsAndDefaultStatus() {
        User user = new User("A", "B", 20, "a@test.com", "+380500000001", "pwd", Role.PARENT);

        User saved = userRepository.saveAndFlush(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreateDate()).isNotNull();
        assertThat(saved.getUpdateDate()).isNotNull();
        assertThat(saved.getUserStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void userDerivedQueries() {
        User u1 = user(Role.PARENT, "1", null);
        User u2 = user(Role.PARENT, "2", null);
        em.flush();

        assertThat(userRepository.findByEmail(u1.getEmail())).contains(u1);
        assertThat(userRepository.existsByEmail(u1.getEmail())).isTrue();
        assertThat(userRepository.existsByEmail("nobody@test.com")).isFalse();
        assertThat(userRepository.existsByPhoneNumber(u2.getPhoneNumber())).isTrue();
        assertThat(userRepository.existsByEmailAndIdNot(u1.getEmail(), u1.getId())).isFalse();
        assertThat(userRepository.existsByEmailAndIdNot(u1.getEmail(), u2.getId())).isTrue();
        assertThat(userRepository.existsByPhoneNumberAndIdNot(u2.getPhoneNumber(), u2.getId())).isFalse();
        assertThat(userRepository.existsByPhoneNumberAndIdNot(u2.getPhoneNumber(), u1.getId())).isTrue();
    }

    @Test
    void userEmailIsUniqueInDatabase() {
        user(Role.PARENT, "1", null);
        User duplicate = TestData.user(null, Role.PARENT, "2");
        duplicate.setEmail("user1@test.com");

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void userPhoneIsUniqueInDatabase() {
        user(Role.PARENT, "1", null);
        User duplicate = TestData.user(null, Role.PARENT, "2");
        duplicate.setPhoneNumber(TestData.phone("1"));

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void schoolDerivedQueries() {
        School s1 = school("1");
        School s2 = school("2");
        em.flush();

        assertThat(schoolRepository.findByName("School 1")).contains(s1);
        assertThat(schoolRepository.findByEmail(s2.getEmail())).contains(s2);
        assertThat(schoolRepository.findByPhoneNumber(s1.getPhoneNumber())).contains(s1);
        assertThat(schoolRepository.existsByAddress("Address 2")).isTrue();
        assertThat(schoolRepository.existsByEmailAndIdNot(s1.getEmail(), s1.getId())).isFalse();
        assertThat(schoolRepository.existsByEmailAndIdNot(s1.getEmail(), s2.getId())).isTrue();
        assertThat(schoolRepository.existsByPhoneNumberAndIdNot(s1.getPhoneNumber(), s2.getId())).isTrue();
    }

    @Test
    void childDetailsSharesIdWithChildUserAndStoresParents() {
        School school = school("1");
        User child = user(Role.CHILD, "10", school);
        User p1 = user(Role.PARENT, "20", school);
        User p2 = user(Role.PARENT, "21", school);
        ClassRoom classRoom = em.persist(new ClassRoom("1-A", new ArrayList<>(), user(Role.TEACHER, "30", school), school, 20));

        childDetailsRepository.saveAndFlush(new ChildDetails(child, school, classRoom, new ArrayList<>(List.of(p1, p2))));
        em.clear();

        ChildDetails loaded = childDetailsRepository.findById(child.getId()).orElseThrow();
        assertThat(loaded.getId()).isEqualTo(child.getId());
        assertThat(loaded.getParents()).extracting(User::getId).containsExactlyInAnyOrder(p1.getId(), p2.getId());
        assertThat(childDetailsRepository.countByClassRoomId(classRoom.getId())).isEqualTo(1);
    }

    @Test
    void classRoomKeepsChildrenViaChildSide() {
        School school = school("1");
        ClassRoom classRoom = em.persist(new ClassRoom("1-A", new ArrayList<>(), user(Role.TEACHER, "30", school), school, 20));
        em.persist(new ChildDetails(user(Role.CHILD, "10", school), school, classRoom, new ArrayList<>()));
        em.persist(new ChildDetails(user(Role.CHILD, "11", school), school, classRoom, new ArrayList<>()));
        em.flush();
        em.clear();

        assertThat(classRoomRepository.findById(classRoom.getId()).orElseThrow().getChildren()).hasSize(2);
    }
}
