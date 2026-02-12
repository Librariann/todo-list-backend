package com.librarian.todo_list.challenges.listener;

import com.librarian.todo_list.challenges.service.ChallengeProgressService;
import com.librarian.todo_list.goals.event.GoalCompletedEvent;
import com.librarian.todo_list.todos.event.TodoCompletedEvent;
import com.librarian.todo_list.habits.event.HabitCompletedEvent;
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

    @EventListener
    @Async
    @Transactional
    public void handleGoalCompleted(GoalCompletedEvent event) {
        try {
            log.info("GOALS 완료 이벤트 처리 시작 - 사용자: {}, GOAL: {}",
                    event.getUser().getId(), event.getGoals().getId());

            challengeProgressService.updateGoalsChallengeProgress(
                    event.getUser(), event.getCompletedDate());

            log.info("GOALS 완료 이벤트 처리 완료 - 사용자: {}", event.getUser().getId());
        } catch (Exception e) {
            log.error("GOALS 완료 이벤트 처리 중 오류 발생 - 사용자: {}, 오류: {}",
                    event.getUser().getId(), e.getMessage(), e);
        }
    }
    @EventListener
    @Async
    @Transactional
    public void handleHabitCompleted(HabitCompletedEvent event) {
        try {
            log.info("HABITS 완료 이벤트 처리 시작 - 사용자: {}, HABIT_LOG: {}",
                    event.getUser().getId(), event.getHabitLog().getId());

            challengeProgressService.updateHabitsChallengeProgress(
                    event.getUser(), event.getCompletedDate());

            log.info("HABITS 완료 이벤트 처리 완료 - 사용자: {}", event.getUser().getId());
        } catch (Exception e) {
            log.error("HABITS 완료 이벤트 처리 중 오류 발생 - 사용자: {}, 오류: {}",
                    event.getUser().getId(), e.getMessage(), e);
        }
    }
}