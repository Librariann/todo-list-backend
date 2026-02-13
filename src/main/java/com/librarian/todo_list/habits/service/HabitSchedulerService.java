package com.librarian.todo_list.habits.service;

import com.librarian.todo_list.habits.entity.Habits;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitSchedulerService {

    private final HabitsService habitsService;
    private final HabitStreaksService habitStreaksService;

    /**
     * 매일 자정 직후 실행 (00:10)
     * 어제 달성 여부를 확인해 streak 업데이트
     * habit_log는 삭제하지 않음 — 잔디 히스토리로 보존
     */
    @Scheduled(cron = "0 10 0 * * ?")
    @Transactional
    public void processDailyStreakUpdate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("습관 스트릭 일일 업데이트 시작: yesterday={}", yesterday);

        try {
            List<Habits> activeHabits = habitsService.findAllActiveHabits();
            log.info("활성 습관 수: {}", activeHabits.size());

            for (Habits habit : activeHabits) {
                try {
                    User user = habit.getUser();
                    habitStreaksService.updateStreak(habit, user, yesterday);
                } catch (Exception e) {
                    log.error("습관 스트릭 업데이트 오류 - habitId={}, error={}", habit.getId(), e.getMessage(), e);
                }
            }

            log.info("습관 스트릭 일일 업데이트 완료: processed={}", activeHabits.size());
        } catch (Exception e) {
            log.error("습관 스트릭 일일 업데이트 중 전체 오류 발생", e);
        }
    }
}
