package com.librarian.todo_list.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET =
            "myTestSecretKeyForJWTTokenGenerationAndValidationPleaseChangeThis12345678";
    private static final long ACCESS_EXPIRATION = 86400000L;   // 24h
    private static final long REFRESH_EXPIRATION = 604800000L; // 7d
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_SESSION_ID = "session-abc-123";

    @BeforeEach
    void setUp() {
        given(jwtProperties.getSecret()).willReturn(SECRET);
        given(jwtProperties.getExpiration()).willReturn(ACCESS_EXPIRATION);
        given(jwtProperties.getRefreshExpiration()).willReturn(REFRESH_EXPIRATION);
    }

    // -----------------------------------------------------------------------
    // generateAccessToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("이메일로 액세스 토큰 생성 성공")
    void generateAccessToken_withEmail_returnsNonBlankToken() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL);

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("이메일+세션ID로 액세스 토큰 생성 성공")
    void generateAccessToken_withEmailAndSessionId_returnsNonBlankToken() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL, TEST_SESSION_ID);

        assertThat(token).isNotBlank();
    }

    // -----------------------------------------------------------------------
    // generateRefreshToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("이메일로 리프레시 토큰 생성 성공")
    void generateRefreshToken_withEmail_returnsNonBlankToken() {
        String token = jwtTokenProvider.generateRefreshToken(TEST_EMAIL);

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("이메일+세션ID로 리프레시 토큰 생성 성공")
    void generateRefreshToken_withEmailAndSessionId_returnsNonBlankToken() {
        String token = jwtTokenProvider.generateRefreshToken(TEST_EMAIL, TEST_SESSION_ID);

        assertThat(token).isNotBlank();
    }

    // -----------------------------------------------------------------------
    // getEmailFromToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 토큰에서 이메일 추출 성공")
    void getEmailFromToken_withValidToken_returnsCorrectEmail() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL);

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo(TEST_EMAIL);
    }

    // -----------------------------------------------------------------------
    // getSessionIdFromToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("세션ID 포함 토큰에서 세션ID 추출 성공")
    void getSessionIdFromToken_withSessionId_returnsCorrectSessionId() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL, TEST_SESSION_ID);

        String sessionId = jwtTokenProvider.getSessionIdFromToken(token);

        assertThat(sessionId).isEqualTo(TEST_SESSION_ID);
    }

    @Test
    @DisplayName("세션ID 미포함 토큰에서 세션ID 추출 시 null 반환")
    void getSessionIdFromToken_withoutSessionId_returnsNull() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL);

        String sessionId = jwtTokenProvider.getSessionIdFromToken(token);

        assertThat(sessionId).isNull();
    }

    // -----------------------------------------------------------------------
    // validateToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_withValidToken_returnsTrue() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL);

        boolean result = jwtTokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 실패")
    void validateToken_withMalformedToken_returnsFalse() {
        boolean result = jwtTokenProvider.validateToken("not.a.valid.jwt.token");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("빈 토큰 검증 실패")
    void validateToken_withEmptyToken_returnsFalse() {
        boolean result = jwtTokenProvider.validateToken("");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateToken_withExpiredToken_returnsFalse() {
        // expiration = 1ms → 즉시 만료
        given(jwtProperties.getExpiration()).willReturn(1L);
        String expiredToken = jwtTokenProvider.generateAccessToken(TEST_EMAIL);

        // 잠시 대기해서 만료시킴
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        boolean result = jwtTokenProvider.validateToken(expiredToken);

        assertThat(result).isFalse();
    }

    // -----------------------------------------------------------------------
    // getExpirationDateFromToken
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("토큰에서 만료 일시 추출 성공")
    void getExpirationDateFromToken_withValidToken_returnsDate() {
        String token = jwtTokenProvider.generateAccessToken(TEST_EMAIL);

        var expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate.getTime()).isGreaterThan(System.currentTimeMillis());
    }
}
