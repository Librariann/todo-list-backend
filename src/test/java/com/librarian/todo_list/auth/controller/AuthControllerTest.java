package com.librarian.todo_list.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarian.todo_list.SecurityConfig;
import com.librarian.todo_list.auth.dto.LoginRequest;
import com.librarian.todo_list.auth.dto.LoginResponse;
import com.librarian.todo_list.auth.service.AuthService;
import com.librarian.todo_list.auth.service.SessionService;
import com.librarian.todo_list.config.TestSecurityConfig;
import com.librarian.todo_list.security.jwt.JwtAuthenticationFilter;
import com.librarian.todo_list.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController 슬라이스 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private LoginResponse buildLoginResponse() {
        return LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .nickname("testuser")
                .email("test@example.com")
                .build();
    }

    // -----------------------------------------------------------------------
    // POST /api/auth/login
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 자격증명으로 로그인 요청 시 200 응답")
    void login_withValidRequest_returns200WithTokens() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password1!")
                .build();

        given(authService.login(any(LoginRequest.class))).willReturn(buildLoginResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.nickname").value("testuser"));
    }

    @Test
    @DisplayName("이메일 누락 시 400 응답")
    void login_withMissingEmail_returns400() throws Exception {
        String requestBody = "{\"password\": \"Password1!\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 누락 시 400 응답")
    void login_withMissingPassword_returns400() throws Exception {
        String requestBody = "{\"email\": \"test@example.com\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 자격증명으로 로그인 시 서비스 예외가 전파됨")
    void login_withInvalidCredentials_propagatesServiceException() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("WrongPassword!")
                .build();

        given(authService.login(any(LoginRequest.class)))
                .willThrow(new BadCredentialsException("비밀번호가 일치하지 않습니다"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // -----------------------------------------------------------------------
    // POST /api/auth/refresh
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 리프레시 토큰으로 토큰 갱신 요청 시 200 응답")
    void refreshToken_withValidToken_returns200() throws Exception {
        given(authService.refreshToken("valid-refresh-token")).willReturn(buildLoginResponse());

        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    // -----------------------------------------------------------------------
    // POST /api/auth/logout
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Bearer 토큰으로 로그아웃 요청 시 200 응답")
    void logout_withBearerToken_returns200() throws Exception {
        given(jwtTokenProvider.validateToken("valid-token")).willReturn(true);
        given(jwtTokenProvider.getSessionIdFromToken("valid-token")).willReturn("session-abc");

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Authorization 헤더 없이 로그아웃 요청 시 200 응답 (관대한 처리)")
    void logout_withoutAuthHeader_returns200Gracefully() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", ""))
                .andExpect(status().isOk());
    }
}
