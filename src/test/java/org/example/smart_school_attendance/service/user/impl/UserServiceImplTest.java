package org.example.smart_school_attendance.service.user.impl;

import org.example.smart_school_attendance.TestData;
import org.example.smart_school_attendance.dto.security.UserAuthDto;
import org.example.smart_school_attendance.dto.user.request.AdminCreateUserRequestDTO;
import org.example.smart_school_attendance.dto.user.request.PublicRegisterRequestDTO;
import org.example.smart_school_attendance.dto.user.request.UserUpdateDTO;
import org.example.smart_school_attendance.dto.user.response.UserDetailsResponseDTO;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.entity.user.User;
import org.example.smart_school_attendance.entity.user.UserStatus;
import org.example.smart_school_attendance.mapper.UserMapper;
import org.example.smart_school_attendance.repository.UserRepository;
import org.example.smart_school_attendance.service.school.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private SchoolService schoolService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, userMapper, schoolService);
    }

    private void saveReturnsArgumentWithId() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(100L);
            return u;
        });
    }

    @Nested
    class Create {

        @Test
        void publicRegistrationCreatesPendingParentWithEncodedPassword() {
            School school = TestData.school(1L, "1");
            PublicRegisterRequestDTO request = TestData.registerRequest("1", 1L);
            when(schoolService.entityById(1L)).thenReturn(school);
            when(passwordEncoder.encode(request.password())).thenReturn("ENCODED");
            saveReturnsArgumentWithId();

            userService.create(request);

            ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(saved.capture());
            assertThat(saved.getValue().getRole()).isEqualTo(Role.PARENT);
            assertThat(saved.getValue().getUserStatus()).isEqualTo(UserStatus.PENDING);
            assertThat(saved.getValue().getPassword()).isEqualTo("ENCODED");
            assertThat(saved.getValue().getSchool()).isSameAs(school);
            assertThat(saved.getValue().getEmail()).isEqualTo(request.email());
        }

        @Test
        void adminCreateUsesRoleFromRequest() {
            AdminCreateUserRequestDTO request = TestData.adminCreateRequest("1", Role.TEACHER, 1L);
            when(schoolService.entityById(1L)).thenReturn(TestData.school(1L, "1"));
            when(passwordEncoder.encode(any())).thenReturn("ENCODED");
            saveReturnsArgumentWithId();
            UserDetailsResponseDTO response = mock(UserDetailsResponseDTO.class);
            when(userMapper.toUserDetailsResponseDTO(any(User.class))).thenReturn(response);

            assertThat(userService.createUserByAdmin(request)).isSameAs(response);

            ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(saved.capture());
            assertThat(saved.getValue().getRole()).isEqualTo(Role.TEACHER);
        }

        @Test
        void adminCreateWithNullRoleIsRejected() {
            AdminCreateUserRequestDTO request = TestData.adminCreateRequest("1", null, 1L);

            assertThatThrownBy(() -> userService.createUserByAdmin(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Create user role can't be null");
            verify(userRepository, never()).save(any());
        }

        @Test
        void duplicateEmailIsRejected() {
            PublicRegisterRequestDTO request = TestData.registerRequest("1", 1L);
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
            verify(userRepository, never()).save(any());
        }

        @Test
        void duplicatePhoneIsRejected() {
            PublicRegisterRequestDTO request = TestData.registerRequest("1", 1L);
            when(userRepository.existsByPhoneNumber(request.phoneNumber())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Authenticate {

        @Test
        void returnsAuthDataForValidCredentials() {
            User user = TestData.user(5L, Role.TEACHER, "5");
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("secret", user.getPassword())).thenReturn(true);

            UserAuthDto auth = userService.authenticate(user.getEmail(), "secret");

            assertThat(auth).isEqualTo(new UserAuthDto(5L, user.getEmail(), Role.TEACHER, UserStatus.ACTIVE));
        }

        @Test
        void wrongPasswordIsRejected() {
            User user = TestData.user(5L, Role.TEACHER, "5");
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.authenticate(user.getEmail(), "wrong"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void unknownEmailIsRejected() {
            when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.authenticate("nobody@test.com", "secret"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  "})
        void blankEmailOrPasswordIsRejected(String blank) {
            assertThatThrownBy(() -> userService.authenticate(blank, "secret"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> userService.authenticate("a@test.com", blank))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void updatesOnlyProvidedFields() {
            User user = TestData.user(1L, Role.PARENT, "1");
            String oldPhone = user.getPhoneNumber();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot("new@test.com", 1L)).thenReturn(false);

            userService.update(1L, UserUpdateDTO.builder().email("new@test.com").build());

            assertThat(user.getEmail()).isEqualTo("new@test.com");
            assertThat(user.getPhoneNumber()).isEqualTo(oldPhone);
            assertThat(user.getPassword()).isEqualTo("encoded-password");
            verify(userRepository).save(user);
        }

        @Test
        void emailTakenByAnotherUserIsRejected() {
            User user = TestData.user(1L, Role.PARENT, "1");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot("taken@test.com", 1L)).thenReturn(true);

            assertThatThrownBy(() -> userService.update(1L, UserUpdateDTO.builder().email("taken@test.com").build()))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(userRepository, never()).save(any());
        }

        @Test
        void phoneTakenByAnotherUserIsRejected() {
            User user = TestData.user(1L, Role.PARENT, "1");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByPhoneNumberAndIdNot("+380999999999", 1L)).thenReturn(true);

            assertThatThrownBy(() -> userService.update(1L, UserUpdateDTO.builder().phoneNumber("+380999999999").build()))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(userRepository, never()).save(any());
        }

        @Test
        void unknownUserIsRejected() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(1L, UserUpdateDTO.builder().email("x@test.com").build()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    class Lookups {

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0L, -1L})
        void invalidIdIsRejected(Long id) {
            assertThatThrownBy(() -> userService.findById(id)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> userService.entityById(id)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> userService.updateStatus(id, UserStatus.ACTIVE)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void findByIdOfMissingUserIsRejected() {
            when(userRepository.findById(9L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findById(9L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        void findAllWithNullPageableIsRejected() {
            assertThatThrownBy(() -> userService.findAll(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void findAllDelegatesToRepository() {
            when(userRepository.findAll(Pageable.ofSize(10))).thenReturn(org.springframework.data.domain.Page.empty());

            assertThat(userService.findAll(Pageable.ofSize(10))).isEmpty();
        }

        @Test
        void findAllByIdsWithEmptyListIsRejected() {
            assertThatThrownBy(() -> userService.findAllByIds(List.of())).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> userService.findAllByIds(null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class UpdateStatus {

        @Test
        void changesStatus() {
            User user = TestData.user(1L, Role.PARENT, "1");
            user.setUserStatus(UserStatus.PENDING);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.updateStatus(1L, UserStatus.ACTIVE);

            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            verify(userRepository).save(user);
        }

        @Test
        void nullStatusIsRejected() {
            assertThatThrownBy(() -> userService.updateStatus(1L, null)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
