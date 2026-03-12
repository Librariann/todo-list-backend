package com.librarian.todo_list.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarian.todo_list.SecurityConfig;
import com.librarian.todo_list.security.jwt.JwtAuthenticationFilter;
import com.librarian.todo_list.config.TestSecurityConfig;

import com.librarian.todo_list.support.WithMockCustomUser;
import com.librarian.todo_list.user.dto.UserRegistrationRequest;
import com.librarian.todo_list.user.dto.UserResponse;
import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.user.service.UserService;
import com.librarian.todo_list.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
@Import(TestSecurityConfig.class)
@DisplayName("UserController 슬라이스 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUserResponse = UserResponse.builder()
                .id(1L)
                .nickname("testuser")
                .email("test@example.com")
                .name("TestName")
                .status(User.UserStatus.ACTIVE)
                .role(User.UserRole.USER)
                .build();
    }

    // -----------------------------------------------------------------------
    // GET /api/users/health
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("헬스 체크 엔드포인트 200 응답")
    void healthCheck_returns200() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // -----------------------------------------------------------------------
    // POST /api/users/register
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 요청으로 회원가입 시 201 응답")
    void registerUser_withValidRequest_returns201() throws Exception {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .nickname("newuser")
                .name("NewUser")
                .email("newuser@example.com")
                .password("Password1!")
                .confirmPassword("Password1!")
                .role(User.UserRole.USER)
                .build();

        given(userService.registerUser(any(UserRegistrationRequest.class))).willReturn(testUserResponse);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value("testuser"));
    }

    @Test
    @DisplayName("닉네임 누락 시 400 응답")
    void registerUser_withMissingNickname_returns400() throws Exception {
        String invalidRequest = """
                {
                    "name": "NewUser",
                    "email": "newuser@example.com",
                    "password": "Password1!",
                    "confirmPassword": "Password1!"
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 회원가입 시 400 응답")
    void registerUser_withInvalidEmail_returns400() throws Exception {
        String invalidRequest = """
                {
                    "nickname": "newuser",
                    "name": "NewUser",
                    "email": "not-an-email",
                    "password": "Password1!",
                    "confirmPassword": "Password1!"
                }
                """;

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("중복 닉네임으로 회원가입 시 서비스 예외가 409로 처리됨")
    void registerUser_withDuplicateNickname_returns409() throws Exception {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .nickname("existinguser")
                .name("NewUser")
                .email("newuser@example.com")
                .password("Password1!")
                .confirmPassword("Password1!")
                .role(User.UserRole.USER)
                .build();

        given(userService.registerUser(any()))
                .willThrow(new UserAlreadyExistsException("이미 사용 중인 사용자명입니다: existinguser"));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -----------------------------------------------------------------------
    // GET /api/users/check-username/{username}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("사용 가능한 닉네임 확인 시 200 응답에 true 반환")
    void checkNicknameAvailability_withAvailableNickname_returns200WithTrue() throws Exception {
        given(userService.isNicknameExists("availableuser")).willReturn(false);

        mockMvc.perform(get("/api/users/check-username/availableuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("이미 사용 중인 닉네임 확인 시 200 응답에 false 반환")
    void checkNicknameAvailability_withUnavailableNickname_returns200WithFalse() throws Exception {
        given(userService.isNicknameExists("existinguser")).willReturn(true);

        mockMvc.perform(get("/api/users/check-username/existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    // -----------------------------------------------------------------------
    // GET /api/users/check-email/{email}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("사용 가능한 이메일 확인 시 200 응답에 true 반환")
    void checkEmailAvailability_withAvailableEmail_returns200WithTrue() throws Exception {
        given(userService.isEmailExists(anyString())).willReturn(false);

        mockMvc.perform(get("/api/users/check-email/new@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("이미 사용 중인 이메일 확인 시 200 응답에 false 반환")
    void checkEmailAvailability_withUnavailableEmail_returns200WithFalse() throws Exception {
        given(userService.isEmailExists(anyString())).willReturn(true);

        mockMvc.perform(get("/api/users/check-email/existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    // -----------------------------------------------------------------------
    // GET /api/users/{id}
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하는 ID로 사용자 조회 시 200 응답")
    void getUserById_withExistingId_returns200() throws Exception {
        given(userService.findById(1L)).willReturn(Optional.of(testUserResponse));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value("testuser"));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 시 404 응답")
    void getUserById_withNonExistingId_returns404() throws Exception {
        given(userService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -----------------------------------------------------------------------
    // GET /api/users/active
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("활성 사용자 목록 조회 시 200 응답")
    void getAllActiveUsers_returns200WithList() throws Exception {
        given(userService.findAllActiveUsers()).willReturn(List.of(testUserResponse));

        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].nickname").value("testuser"));
    }

    // -----------------------------------------------------------------------
    // GET /api/users/me
    // -----------------------------------------------------------------------

    @Test
    @WithMockCustomUser(nickname = "testuser", email = "test@example.com")
    @DisplayName("인증된 사용자가 /me 조회 시 200 응답")
    void me_withAuthenticatedUser_returns200() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value("testuser"));
    }
}
