package com.librarian.todo_list.habits.service;

import com.librarian.todo_list.habits.entity.HabitLog;
import com.librarian.todo_list.habits.entity.HabitStreaks;
import com.librarian.todo_list.habits.entity.Habits;
import com.librarian.todo_list.habits.repository.HabitLogRepository;
import com.librarian.todo_list.habits.repository.HabitStreaksRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitStreaksService {

    private final HabitStreaksRepository habitStreaksRepository;
    private final HabitLogRepository habitLogRepository;

    /**
     * 자정 리셋 스케줄러에서 호출
     * 어제 달성 여부를 확인해 streak 업데이트
     */
    @Transactional
    public void updateStreak(Habits habit, User user, LocalDate yesterday) {
        Optional<HabitLog> yesterdayLog = habitLogRepository.findByHabitAndUserAndLogDate(habit, user, yesterday);

        HabitStreaks streaks = habitStreaksRepository.findByHabitAndUser(habit, user)
                .orElseGet(() -> habitStreaksRepository.save(
                        HabitStreaks.builder()
                                .habit(habit)
                                .user(user)
                                .currentStreak(0)
                                .longestStreak(0)
                                .build()
                ));

        boolean achievedYesterday = yesterdayLog.map(HabitLog::getIsAchieved).orElse(false);

        if (achievedYesterday) {
            streaks.setCurrentStreak(streaks.getCurrentStreak() + 1);
            if (streaks.getCurrentStreak() > streaks.getLongestStreak()) {
                streaks.setLongestStreak(streaks.getCurrentStreak());
            }
            log.info("스트릭 증가 - habitId={}, userId={}, streak={}", habit.getId(), user.getId(), streaks.getCurrentStreak());
        } else {
            if (streaks.getCurrentStreak() > 0) {
                log.info("스트릭 초기화 - habitId={}, userId={}, 이전streak={}", habit.getId(), user.getId(), streaks.getCurrentStreak());
                streaks.setCurrentStreak(0);
            }
        }

        habitStreaksRepository.save(streaks);
    }
}
