package com.librarian.todo_list.auth.service;

import com.librarian.todo_list.auth.dto.LoginRequest;
import com.librarian.todo_list.auth.dto.LoginResponse;
import com.librarian.todo_list.security.jwt.JwtTokenProvider;
import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private AuthService authService;

    private User activeUser;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        validLoginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .build();
    }

    // -----------------------------------------------------------------------
    // login
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 자격증명으로 로그인 성공")
    void login_withValidCredentials_returnsLoginResponse() {
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(activeUser));
        given(passwordEncoder.matches("Password1!", "encoded_password")).willReturn(true);
        given(sessionService.createSession(activeUser)).willReturn("session-id-abc");
        given(jwtTokenProvider.generateAccessToken("test@example.com", "session-id-abc"))
                .willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken("test@example.com", "session-id-abc"))
                .willReturn("refresh-token");

        LoginResponse result = authService.login(validLoginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 UsernameNotFoundException 발생")
    void login_withNonExistingEmail_throwsUsernameNotFoundException() {
        given(userRepository.findByEmail("unknown@example.com")).willReturn(Optional.empty());

        LoginRequest request = LoginRequest.builder()
                .email("unknown@example.com")
                .password("Password1!")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown@example.com");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 BadCredentialsException 발생")
    void login_withWrongPassword_throwsBadCredentialsException() {
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(activeUser));
        given(passwordEncoder.matches("WrongPassword!", "encoded_password")).willReturn(false);

        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword!")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("비밀번호");
    }

    @Test
    @DisplayName("비활성화된 계정으로 로그인 시 BadCredentialsException 발생")
    void login_withInactiveUser_throwsBadCredentialsException() {
        User inactiveUser = User.builder()
                .nickname("inactive")
                .email("inactive@example.com")
                .password("encoded_password")
                .role(User.UserRole.USER)
                .status(User.UserStatus.INACTIVE)
                .build();

        given(userRepository.findByEmail("inactive@example.com")).willReturn(Optional.of(inactiveUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        LoginRequest request = LoginRequest.builder()
                .email("inactive@example.com")
                .password("Password1!")
                .build();

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("비활성화");
    }

    // -----------------------------------------------------------------------
    // refreshToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 리프레시 토큰으로 토큰 갱신 성공")
    void refreshToken_withValidToken_returnsNewLoginResponse() {
        String refreshToken = "valid-refresh-token";

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(refreshToken)).willReturn("test@example.com");
        given(jwtTokenProvider.getSessionIdFromToken(refreshToken)).willReturn("session-abc");
        given(sessionService.isValidSession("session-abc")).willReturn(true);
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(activeUser));
        given(jwtTokenProvider.generateAccessToken("test@example.com", "session-abc"))
                .willReturn("new-access-token");

        LoginResponse result = authService.refreshToken(refreshToken);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        then(sessionService).should().refreshSession("session-abc");
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 시 BadCredentialsException 발생")
    void refreshToken_withInvalidToken_throwsBadCredentialsException() {
        given(jwtTokenProvider.validateToken("invalid-token")).willReturn(false);

        assertThatThrownBy(() -> authService.refreshToken("invalid-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("유효하지 않은");
    }

    @Test
    @DisplayName("만료된 세션의 리프레시 토큰 사용 시 BadCredentialsException 발생")
    void refreshToken_withExpiredSession_throwsBadCredentialsException() {
        String refreshToken = "refresh-with-expired-session";

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(refreshToken)).willReturn("test@example.com");
        given(jwtTokenProvider.getSessionIdFromToken(refreshToken)).willReturn("expired-session");
        given(sessionService.isValidSession("expired-session")).willReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("세션");
    }
}
