package com.librarian.todo_list.challenges.listener;

import com.librarian.todo_list.challenges.service.ChallengeProgressService;
import com.librarian.todo_list.todos.event.TodoCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeEventListener {

    private final ChallengeProgressService challengeProgressService;

    @EventListener
    @Async
    @Transactional
    public void handleTodoCompleted(TodoCompletedEvent event) {
        try {
            log.info("TODO 완료 이벤트 처리 시작 - 사용자: {}, TODO: {}", 
                event.getUser().getId(), event.getTodo().getId());

            challengeProgressService.updateTodosChallengeProgress(
                event.getUser(), event.getCompletedDate());

            log.info("TODO 완료 이벤트 처리 완료 - 사용자: {}", event.getUser().getId());
        } catch (Exception e) {
            log.error("TODO 완료 이벤트 처리 중 오류 발생 - 사용자: {}, 오류: {}", 
                event.getUser().getId(), e.getMessage(), e);
        }
    }
}