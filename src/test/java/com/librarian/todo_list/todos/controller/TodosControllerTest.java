package com.librarian.todo_list.todos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarian.todo_list.SecurityConfig;
import com.librarian.todo_list.security.jwt.JwtAuthenticationFilter;
import com.librarian.todo_list.config.TestSecurityConfig;
import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.exception.TodoStatusChangeException;
import com.librarian.todo_list.support.WithMockCustomUser;
import com.librarian.todo_list.todos.dto.TodosRegistrationRequest;
import com.librarian.todo_list.todos.dto.TodosResponse;
import com.librarian.todo_list.todos.dto.TodosUpdateRequest;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.todos.service.TodosService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TodosController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
@Import(TestSecurityConfig.class)
@DisplayName("TodosController 슬라이스 테스트")
class TodosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodosService todosService;

    private TodosResponse testTodosResponse;
    private final LocalDate targetDate = LocalDate.of(2026, 1, 1);

    @BeforeEach
    void setUp() {
        testTodosResponse = TodosResponse.builder()
                .id(1L)
                .name("테스트 할 일")
                .status(Todos.TodosStatus.READY)
                .orderIndex(1)
                .targetDate(targetDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // -----------------------------------------------------------------------
    // GET /api/todos/{targetDate}
    // -----------------------------------------------------------------------

    @Test
    @WithMockCustomUser
    @DisplayName("인증된 사용자가 날짜로 할 일 목록 조회 시 200 응답")
    void getTodosList_withAuthentication_returns200() throws Exception {
        given(todosService.getTodos(any(), any(LocalDate.class)))
                .willReturn(List.of(testTodosResponse));

        mockMvc.perform(get("/api/todos/{date}", targetDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("테스트 할 일"))
                .andExpect(jsonPath("$.data[0].status").value("READY"));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("할 일이 없는 날짜 조회 시 빈 배열 반환")
    void getTodosList_withNoTodos_returnsEmptyArray() throws Exception {
        given(todosService.getTodos(any(), any(LocalDate.class))).willReturn(List.of());

        mockMvc.perform(get("/api/todos/{date}", targetDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // -----------------------------------------------------------------------
    // POST /api/todos/register
    // -----------------------------------------------------------------------

    @Test
    @WithMockCustomUser
    @DisplayName("유효한 요청으로 할 일 등록 시 201 응답")
    void registerTodos_withValidBody_returns201() throws Exception {
        TodosRegistrationRequest request = TodosRegistrationRequest.builder()
                .name("새로운 할 일")
                .targetDate(targetDate)
                .build();

        given(todosService.registerTodos(any(TodosRegistrationRequest.class), any()))
                .willReturn(testTodosResponse);

        mockMvc.perform(post("/api/todos/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("테스트 할 일"));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("이름 누락 시 400 응답")
    void registerTodos_withMissingName_returns400() throws Exception {
        String invalidBody = "{\"targetDate\": \"2026-01-01\"}";

        mockMvc.perform(post("/api/todos/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("날짜 누락 시 400 응답")
    void registerTodos_withMissingTargetDate_returns400() throws Exception {
        String invalidBody = "{\"name\": \"할 일 이름\"}";

        mockMvc.perform(post("/api/todos/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("3자 미만 이름으로 등록 시 400 응답")
    void registerTodos_withNameTooShort_returns400() throws Exception {
        TodosRegistrationRequest request = TodosRegistrationRequest.builder()
                .name("짧")
                .targetDate(targetDate)
                .build();

        mockMvc.perform(post("/api/todos/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("중복 이름으로 할 일 등록 시 409 응답")
    void registerTodos_withDuplicateName_returns409() throws Exception {
        TodosRegistrationRequest request = TodosRegistrationRequest.builder()
                .name("중복 할 일")
                .targetDate(targetDate)
                .build();

        given(todosService.registerTodos(any(), any()))
                .willThrow(new CommonAlreadyExistsException("이미 사용중인 할 일 입니다: 중복 할 일"));

        mockMvc.perform(post("/api/todos/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -----------------------------------------------------------------------
    // PATCH /api/todos/{id}
    // -----------------------------------------------------------------------

    @Test
    @WithMockCustomUser
    @DisplayName("유효한 요청으로 할 일 수정 시 200 응답")
    void updateTodos_withValidBody_returns200() throws Exception {
        TodosUpdateRequest request = TodosUpdateRequest.builder()
                .name("수정된 할 일")
                .targetDate(targetDate)
                .build();

        given(todosService.updateTodos(any(TodosUpdateRequest.class), anyLong(), any()))
                .willReturn(testTodosResponse);

        mockMvc.perform(patch("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("존재하지 않는 할 일 수정 시 404 응답")
    void updateTodos_withNonExistingId_returns404() throws Exception {
        TodosUpdateRequest request = TodosUpdateRequest.builder()
                .name("수정된 할 일")
                .build();

        given(todosService.updateTodos(any(), anyLong(), any()))
                .willThrow(new IllegalArgumentException("할 일을 찾을 수 없습니다: 999"));

        mockMvc.perform(patch("/api/todos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -----------------------------------------------------------------------
    // PATCH /api/todos/{id}/status/{status}
    // -----------------------------------------------------------------------

    @Test
    @WithMockCustomUser
    @DisplayName("READY → DONE 상태 변경 시 200 응답")
    void updateStatusTodos_withValidStatus_returns200() throws Exception {
        willDoNothing().given(todosService).updateStatusTodos(any(), anyLong(), any());

        mockMvc.perform(patch("/api/todos/1/status/DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("완료된 할 일 상태 변경 시 400 응답")
    void updateStatusTodos_withDoneStatus_returns400() throws Exception {
        willThrow(new TodoStatusChangeException("이미 완료된 할 일은 상태를 변경할 수 없습니다."))
                .given(todosService).updateStatusTodos(any(), anyLong(), any());

        mockMvc.perform(patch("/api/todos/1/status/READY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -----------------------------------------------------------------------
    // DELETE /api/todos/{id}
    // -----------------------------------------------------------------------

    @Test
    @WithMockCustomUser
    @DisplayName("존재하는 할 일 삭제 시 200 응답")
    void deleteTodos_withValidId_returns200() throws Exception {
        willDoNothing().given(todosService).deleteTodos(anyLong(), any());

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("존재하지 않는 할 일 삭제 시 404 응답")
    void deleteTodos_withNonExistingId_returns404() throws Exception {
        willThrow(new IllegalArgumentException("할 일을 찾을 수 없습니다: 999"))
                .given(todosService).deleteTodos(anyLong(), any());

        mockMvc.perform(delete("/api/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
