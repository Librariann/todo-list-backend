package com.librarian.todo_list.todos.service;

import com.librarian.todo_list.exception.CommonAlreadyExistsException;
import com.librarian.todo_list.exception.TodoStatusChangeException;
import com.librarian.todo_list.todos.dto.TodosOrderUpdateRequest;
import com.librarian.todo_list.todos.dto.TodosRegistrationRequest;
import com.librarian.todo_list.todos.dto.TodosResponse;
import com.librarian.todo_list.todos.dto.TodosUpdateRequest;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.todos.event.TodoCompletedEvent;
import com.librarian.todo_list.todos.repository.TodosRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodosService {

    private final TodosRepository todosRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    //get list
    public List<TodosResponse> getTodos(User user, LocalDate targetDate) {
        return todosRepository.findByUserIdAndTargetDate(user.getId(), targetDate)
                .stream()
                .map(TodosResponse::from)
                .toList();
    }

    @Transactional
    public TodosResponse registerTodos(TodosRegistrationRequest request, User user) {
        Optional<Todos> max = todosRepository.findTopByUserAndTargetDateOrderByOrderIndexDesc(user, request.getTargetDate());

        // 중복 할 일 확인
        validateTodosUniqueness(request.getName());
        int orderIndex = max.map(Todos::getOrderIndex).orElse(0) + 1;

        // create Todos Entity
        Todos todos = Todos.builder()
                .name(request.getName())
                .user(user)
                .status(Todos.TodosStatus.READY)
                .orderIndex(orderIndex)
                .targetDate(request.getTargetDate())
                .build();

        // 사용자 저장
        Todos savedTodos = todosRepository.save(todos);
        return TodosResponse.from(savedTodos);
    }

    // 수정
    @Transactional
    public TodosResponse updateTodos(TodosUpdateRequest request, Long id, User user) {
        Todos getTodos = todosRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다: " + id));

        if(request.getName() != null
                && !request.getName().isBlank()
                && todosRepository.existsByNameAndIdNot(request.getName(), id)
        ) {
            throw new CommonAlreadyExistsException("이미 사용중인 할 일명 입니다: " + request.getName());
        }

        getTodos.update(request);

        return TodosResponse.from(getTodos);
    }

    //상태 변경
    @Transactional
    public void updateStatusTodos(Todos.TodosStatus status, Long id, User user) {
        Todos getTodos = todosRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다: " + id));

        if(getTodos.getStatus() == Todos.TodosStatus.DONE && status != Todos.TodosStatus.DONE) {
            throw new TodoStatusChangeException(
                "이미 완료된 할 일은 상태를 변경할 수 없습니다.");
        }

        Todos.TodosStatus previousStatus = getTodos.getStatus();
        getTodos.setStatus(status);

        if (status == Todos.TodosStatus.DONE && previousStatus != Todos.TodosStatus.DONE) {
            log.info("TODO 완료 이벤트 발행 - 사용자: {}, TODO: {}", user.getId(), id);
            applicationEventPublisher.publishEvent(
                new TodoCompletedEvent(user, getTodos, LocalDate.now())
            );
        }
    }

    //순서변경
    @Transactional
    public void updateIndexTodos(TodosOrderUpdateRequest request, User user) {
        List<Todos> getTodos = todosRepository.findByUserIdAndTargetDate(user.getId(), request.getTargetDate());

//        getTodos.setStatus(status);
    }

    // 삭제
    @Transactional
    public void deleteTodos(Long id, User user) {
        Todos getTodos = todosRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다: " + id));
        todosRepository.delete(getTodos);
    }
//     이미 사용중인 Todos 확인
    private void validateTodosUniqueness(String name) {
        if (todosRepository.existsByName(name)) {
            throw new CommonAlreadyExistsException("이미 사용중인 할 일 입니다: " + name);
        }
    }
}