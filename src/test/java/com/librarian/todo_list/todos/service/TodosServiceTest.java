package com.librarian.todo_list.todos.service;

import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.exception.TodoStatusChangeException;
import com.librarian.todo_list.todos.dto.TodosRegistrationRequest;
import com.librarian.todo_list.todos.dto.TodosResponse;
import com.librarian.todo_list.todos.dto.TodosUpdateRequest;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.todos.repository.TodosRepository;
import com.librarian.todo_list.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodosService 단위 테스트")
class TodosServiceTest {

    @Mock
    private TodosRepository todosRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private TodosService todosService;

    private User testUser;
    private Todos testTodo;
    private LocalDate targetDate;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        targetDate = LocalDate.of(2026, 1, 1);

        testTodo = Todos.builder()
                .name("테스트 할 일")
                .user(testUser)
                .status(Todos.TodosStatus.READY)
                .orderIndex(1)
                .targetDate(targetDate)
                .build();
    }

    // -----------------------------------------------------------------------
    // getTodos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("날짜와 사용자로 할 일 목록 조회 성공")
    void getTodos_returnsListForUserAndDate() {
        given(todosRepository.findByUserIdAndTargetDate(any(), any()))
                .willReturn(List.of(testTodo));

        List<TodosResponse> result = todosService.getTodos(testUser, targetDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 할 일");
    }

    @Test
    @DisplayName("할 일이 없는 날짜 조회 시 빈 리스트 반환")
    void getTodos_withNoTodos_returnsEmptyList() {
        given(todosRepository.findByUserIdAndTargetDate(any(), any()))
                .willReturn(List.of());

        List<TodosResponse> result = todosService.getTodos(testUser, targetDate);

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // registerTodos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 요청으로 할 일 등록 성공")
    void registerTodos_withValidRequest_returnsTodoResponse() {
        TodosRegistrationRequest request = TodosRegistrationRequest.builder()
                .name("새 할 일")
                .targetDate(targetDate)
                .build();

        given(todosRepository.existsByName("새 할 일")).willReturn(false);
        given(todosRepository.findTopByUserAndTargetDateOrderByOrderIndexDesc(any(), any()))
                .willReturn(Optional.empty());
        given(todosRepository.save(any(Todos.class))).willReturn(testTodo);

        TodosResponse result = todosService.registerTodos(request, testUser);

        assertThat(result).isNotNull();
        then(todosRepository).should().save(any(Todos.class));
    }

    @Test
    @DisplayName("기존 할 일이 있을 때 orderIndex가 +1 증가")
    void registerTodos_withExistingTodo_incrementsOrderIndex() {
        Todos existingTodo = Todos.builder()
                .name("기존 할 일")
                .user(testUser)
                .orderIndex(3)
                .targetDate(targetDate)
                .build();

        TodosRegistrationRequest request = TodosRegistrationRequest.builder()
                .name("새 할 일")
                .targetDate(targetDate)
                .build();

        given(todosRepository.existsByName("새 할 일")).willReturn(false);
        given(todosRepository.findTopByUserAndTargetDateOrderByOrderIndexDesc(any(), any()))
                .willReturn(Optional.of(existingTodo));
        given(todosRepository.save(any(Todos.class))).willReturn(testTodo);

        todosService.registerTodos(request, testUser);

        then(todosRepository).should().save(any(Todos.class));
    }

    @Test
    @DisplayName("중복 이름으로 할 일 등록 시 CommonAlreadyExistsException 발생")
    void registerTodos_withDuplicateName_throwsException() {
        TodosRegistrationRequest request = TodosRegistrationRequest.builder()
                .name("테스트 할 일")
                .targetDate(targetDate)
                .build();

        given(todosRepository.existsByName("테스트 할 일")).willReturn(true);

        assertThatThrownBy(() -> todosService.registerTodos(request, testUser))
                .isInstanceOf(CommonAlreadyExistsException.class);

        then(todosRepository).should(never()).save(any());
    }

    // -----------------------------------------------------------------------
    // updateTodos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 요청으로 할 일 수정 성공")
    void updateTodos_withValidRequest_returnsTodoResponse() {
        TodosUpdateRequest request = TodosUpdateRequest.builder()
                .name("수정된 할 일")
                .targetDate(targetDate)
                .build();

        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));
        given(todosRepository.existsByNameAndIdNot("수정된 할 일", 1L)).willReturn(false);

        TodosResponse result = todosService.updateTodos(request, 1L, testUser);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 할 일 수정 시 IllegalArgumentException 발생")
    void updateTodos_withNonExistingId_throwsIllegalArgumentException() {
        TodosUpdateRequest request = TodosUpdateRequest.builder()
                .name("수정된 할 일")
                .build();

        given(todosRepository.findByIdAndUser(999L, testUser)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todosService.updateTodos(request, 999L, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("다른 할 일과 중복되는 이름으로 수정 시 CommonAlreadyExistsException 발생")
    void updateTodos_withDuplicateNameForOtherTodo_throwsException() {
        TodosUpdateRequest request = TodosUpdateRequest.builder()
                .name("다른 할 일 이름")
                .build();

        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));
        given(todosRepository.existsByNameAndIdNot("다른 할 일 이름", 1L)).willReturn(true);

        assertThatThrownBy(() -> todosService.updateTodos(request, 1L, testUser))
                .isInstanceOf(CommonAlreadyExistsException.class);
    }

    // -----------------------------------------------------------------------
    // updateStatusTodos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("READY → PROCESS 상태 변경 성공")
    void updateStatusTodos_fromReadyToProcess_updatesStatus() {
        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));

        todosService.updateStatusTodos(Todos.TodosStatus.PROCESS, 1L, testUser);

        assertThat(testTodo.getStatus()).isEqualTo(Todos.TodosStatus.PROCESS);
        then(applicationEventPublisher).should(never()).publishEvent(any());
    }

    @Test
    @DisplayName("READY → DONE 상태 변경 시 완료 이벤트 발행")
    void updateStatusTodos_fromReadyToDone_publishesCompletedEvent() {
        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));

        todosService.updateStatusTodos(Todos.TodosStatus.DONE, 1L, testUser);

        assertThat(testTodo.getStatus()).isEqualTo(Todos.TodosStatus.DONE);
        then(applicationEventPublisher).should().publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("DONE 상태에서 다른 상태로 변경 시 TodoStatusChangeException 발생")
    void updateStatusTodos_fromDoneToOther_throwsTodoStatusChangeException() {
        testTodo.setStatus(Todos.TodosStatus.DONE);
        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));

        assertThatThrownBy(() ->
                todosService.updateStatusTodos(Todos.TodosStatus.READY, 1L, testUser))
                .isInstanceOf(TodoStatusChangeException.class)
                .hasMessageContaining("완료된 할 일");
    }

    @Test
    @DisplayName("DONE → DONE 상태는 이미 완료이므로 예외 발생")
    void updateStatusTodos_fromDoneToDone_throwsTodoStatusChangeException() {
        testTodo.setStatus(Todos.TodosStatus.DONE);
        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));

        assertThatThrownBy(() ->
                todosService.updateStatusTodos(Todos.TodosStatus.PROCESS, 1L, testUser))
                .isInstanceOf(TodoStatusChangeException.class);
    }

    @Test
    @DisplayName("존재하지 않는 할 일 상태 변경 시 예외 발생")
    void updateStatusTodos_withNonExistingId_throwsException() {
        given(todosRepository.findByIdAndUser(999L, testUser)).willReturn(Optional.empty());

        assertThatThrownBy(() ->
                todosService.updateStatusTodos(Todos.TodosStatus.DONE, 999L, testUser))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // -----------------------------------------------------------------------
    // deleteTodos
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 ID로 할 일 삭제 성공")
    void deleteTodos_withValidId_deletesTodo() {
        given(todosRepository.findByIdAndUser(1L, testUser)).willReturn(Optional.of(testTodo));

        todosService.deleteTodos(1L, testUser);

        then(todosRepository).should().delete(testTodo);
    }

    @Test
    @DisplayName("존재하지 않는 할 일 삭제 시 IllegalArgumentException 발생")
    void deleteTodos_withNonExistingId_throwsIllegalArgumentException() {
        given(todosRepository.findByIdAndUser(999L, testUser)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todosService.deleteTodos(999L, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");

        then(todosRepository).should(never()).delete(any());
    }
}
