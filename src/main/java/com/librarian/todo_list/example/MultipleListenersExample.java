package com.librarian.todo_list.example;

import com.librarian.todo_list.todos.event.TodoCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MultipleListenersExample {

    @EventListener
    @Async
    public void sendNotification(TodoCompletedEvent event) {
        log.info("📱 알림 발송 - 사용자: {}, TODO: {}", 
            event.getUser().getId(), event.getTodo().getName());
    }
    
    @EventListener
    @Async
    public void collectStatistics(TodoCompletedEvent event) {
        log.info("📊 통계 수집 - 사용자: {}, 완료시간: {}", 
            event.getUser().getId(), event.getCompletedDate());
    }
    
    @EventListener
    @Async  
    public void updateRecommendation(TodoCompletedEvent event) {
        log.info("🤖 추천 시스템 업데이트 - 사용자: {}", event.getUser().getId());
    }
}