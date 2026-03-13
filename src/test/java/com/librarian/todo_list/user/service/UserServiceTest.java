package com.librarian.todo_list.user.service;

import com.librarian.todo_list.exception.InvalidPasswordException;
import com.librarian.todo_list.exception.UserAlreadyExistsException;
import com.librarian.todo_list.user.dto.UserRegistrationRequest;
import com.librarian.todo_list.user.dto.UserResponse;
import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .name("TestName")
                .phoneNumber("01012345678")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        validRequest = UserRegistrationRequest.builder()
                .nickname("testuser")
                .name("TestName")
                .email("test@example.com")
                .password("Password1!")
                .confirmPassword("Password1!")
                .phoneNumber("01012345678")
                .role(User.UserRole.USER)
                .build();
    }

    // -----------------------------------------------------------------------
    // registerUser
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 요청으로 회원가입 성공")
    void registerUser_withValidRequest_returnsUserResponse() {
        given(passwordEncoder.encode(anyString())).willReturn("encoded_password");
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(testUser);

        UserResponse result = userService.registerUser(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 불일치 시 InvalidPasswordException 발생")
    void registerUser_withMismatchedPasswords_throwsInvalidPasswordException() {
        UserRegistrationRequest mismatchRequest = UserRegistrationRequest.builder()
                .nickname("testuser")
                .name("TestName")
                .email("test@example.com")
                .password("Password1!")
                .confirmPassword("Different1!")
                .role(User.UserRole.USER)
                .build();

        assertThatThrownBy(() -> userService.registerUser(mismatchRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("일치하지 않습니다");

        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 회원가입 시 UserAlreadyExistsException 발생")
    void registerUser_withDuplicateNickname_throwsUserAlreadyExistsException() {
        given(userRepository.existsByNickname("testuser")).willReturn(true);

        assertThatThrownBy(() -> userService.registerUser(validRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("testuser");

        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 UserAlreadyExistsException 발생")
    void registerUser_withDuplicateEmail_throwsUserAlreadyExistsException() {
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        assertThatThrownBy(() -> userService.registerUser(validRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("test@example.com");

        then(userRepository).should(never()).save(any());
    }

    // -----------------------------------------------------------------------
    // findByNickname
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하는 닉네임으로 사용자 조회 성공")
    void findByNickname_withExistingNickname_returnsUserResponse() {
        given(userRepository.findByNickname("testuser")).willReturn(Optional.of(testUser));

        Optional<UserResponse> result = userService.findByNickname("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getNickname()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("존재하지 않는 닉네임으로 조회 시 빈 Optional 반환")
    void findByNickname_withNonExistingNickname_returnsEmpty() {
        given(userRepository.findByNickname("unknown")).willReturn(Optional.empty());

        Optional<UserResponse> result = userService.findByNickname("unknown");

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // findByEmail
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하는 이메일로 사용자 조회 성공")
    void findByEmail_withExistingEmail_returnsUserResponse() {
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(testUser));

        Optional<UserResponse> result = userService.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    // -----------------------------------------------------------------------
    // findById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하는 ID로 사용자 조회 성공")
    void findById_withExistingId_returnsUserResponse() {
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        Optional<UserResponse> result = userService.findById(1L);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
    void findById_withNonExistingId_returnsEmpty() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        Optional<UserResponse> result = userService.findById(999L);

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // findAllActiveUsers
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("활성 사용자 목록 조회 성공")
    void findAllActiveUsers_returnsActiveUsersList() {
        given(userRepository.findActiveUsers()).willReturn(List.of(testUser));

        List<UserResponse> result = userService.findAllActiveUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNickname()).isEqualTo("testuser");
    }

    // -----------------------------------------------------------------------
    // isNicknameExists / isEmailExists
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("닉네임 존재 확인 - 존재할 때 true 반환")
    void isNicknameExists_whenExists_returnsTrue() {
        given(userRepository.existsByNickname("testuser")).willReturn(true);

        assertThat(userService.isNicknameExists("testuser")).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 확인 - 존재하지 않을 때 false 반환")
    void isNicknameExists_whenNotExists_returnsFalse() {
        given(userRepository.existsByNickname("unknown")).willReturn(false);

        assertThat(userService.isNicknameExists("unknown")).isFalse();
    }

    @Test
    @DisplayName("이메일 존재 확인 - 존재할 때 true 반환")
    void isEmailExists_whenExists_returnsTrue() {
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        assertThat(userService.isEmailExists("test@example.com")).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 확인 - 존재하지 않을 때 false 반환")
    void isEmailExists_whenNotExists_returnsFalse() {
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);

        assertThat(userService.isEmailExists("new@example.com")).isFalse();
    }
}
