package com.librarian.todo_list.habits.service;

import com.librarian.todo_list.habits.dto.HabitsRegistrationRequest;
import com.librarian.todo_list.habits.dto.HabitsResponse;
import com.librarian.todo_list.habits.dto.HabitsUpdateRequest;
import com.librarian.todo_list.habits.entity.HabitLog;
import com.librarian.todo_list.habits.entity.HabitStreaks;
import com.librarian.todo_list.habits.entity.Habits;
import com.librarian.todo_list.habits.event.HabitCompletedEvent;
import com.librarian.todo_list.habits.repository.HabitLogRepository;
import com.librarian.todo_list.habits.repository.HabitStreaksRepository;
import com.librarian.todo_list.habits.repository.HabitsRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitsService {

    private final HabitsRepository habitsRepository;
    private final HabitLogRepository habitLogRepository;
    private final HabitStreaksRepository habitStreaksRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public HabitsResponse createHabit(HabitsRegistrationRequest request, User user) {
        if (habitsRepository.existsByUserAndNameAndIsActiveTrue(user, request.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 활성 습관이 존재합니다: " + request.getName());
        }

        Habits habit = Habits.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .dailyTarget(request.getDailyTarget())
                .unit(request.getUnit())
                .isActive(true)
                .build();

        Habits saved = habitsRepository.save(habit);

        // 스트릭 초기화
        HabitStreaks streaks = HabitStreaks.builder()
                .habit(saved)
                .user(user)
                .currentStreak(0)
                .longestStreak(0)
                .build();
        habitStreaksRepository.save(streaks);

        log.info("습관 생성 완료: habitId={}, name={}, user={}", saved.getId(), saved.getName(), user.getEmail());
        return HabitsResponse.from(saved, null, streaks);
    }

    public List<HabitsResponse> getUserHabits(User user) {
        LocalDate today = LocalDate.now();
        List<Habits> habits = habitsRepository.findByUserAndIsActiveTrueOrderByCreatedAtDesc(user);

        return habits.stream().map(habit -> {
            HabitLog todayLog = habitLogRepository.findByHabitAndLogDate(habit, today).orElse(null);
            HabitStreaks streaks = habitStreaksRepository.findByHabitAndUser(habit, user).orElse(null);
            return HabitsResponse.from(habit, todayLog, streaks);
        }).collect(Collectors.toList());
    }

    @Transactional
    public HabitsResponse updateHabit(Long habitId, HabitsUpdateRequest request, User user) {
        Habits habit = habitsRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new IllegalArgumentException("습관을 찾을 수 없습니다: " + habitId));

        if (request.getName() != null && !request.getName().equals(habit.getName())
                && habitsRepository.existsByUserAndNameAndIsActiveTrue(user, request.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 활성 습관이 존재합니다: " + request.getName());
        }

        if (request.getName() != null) habit.setName(request.getName());
        if (request.getDescription() != null) habit.setDescription(request.getDescription());
        if (request.getDailyTarget() != null) habit.setDailyTarget(request.getDailyTarget());
        if (request.getUnit() != null) habit.setUnit(request.getUnit());

        LocalDate today = LocalDate.now();
        HabitLog todayLog = habitLogRepository.findByHabitAndLogDate(habit, today).orElse(null);
        HabitStreaks streaks = habitStreaksRepository.findByHabitAndUser(habit, user).orElse(null);

        log.info("습관 수정 완료: habitId={}, name={}", habitId, habit.getName());
        return HabitsResponse.from(habit, todayLog, streaks);
    }

    @Transactional
    public void deactivateHabit(Long habitId, User user) {
        Habits habit = habitsRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new IllegalArgumentException("습관을 찾을 수 없습니다: " + habitId));

        habit.setIsActive(false);
        habitsRepository.save(habit);
        log.info("습관 비활성화 완료: habitId={}, name={}", habitId, habit.getName());
    }

    /**
     * 카운터 +1
     * 처음 누르면 오늘 HabitLog 생성, 이후엔 currentCount 증가
     * dailyTarget 달성 시 HabitCompletedEvent 발행
     */
    @Transactional
    public HabitsResponse increment(Long habitId, User user) {
        Habits habit = habitsRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new IllegalArgumentException("습관을 찾을 수 없습니다: " + habitId));

        LocalDate today = LocalDate.now();
        HabitLog log = habitLogRepository.findByHabitAndUserAndLogDate(habit, user, today)
                .orElseGet(() -> habitLogRepository.save(
                        HabitLog.builder()
                                .habit(habit)
                                .user(user)
                                .logDate(today)
                                .currentCount(0)
                                .isAchieved(false)
                                .build()
                ));

        boolean wasAchievedBefore = log.getIsAchieved();
        log.setCurrentCount(log.getCurrentCount() + 1);

        if (!wasAchievedBefore && log.getCurrentCount() >= habit.getDailyTarget()) {
            log.setIsAchieved(true);
            habitLogRepository.save(log);

            eventPublisher.publishEvent(new HabitCompletedEvent(user, log, today));
            this.log.info("습관 달성 이벤트 발행 - habitId={}, userId={}", habitId, user.getId());
        } else {
            habitLogRepository.save(log);
        }

        HabitStreaks streaks = habitStreaksRepository.findByHabitAndUser(habit, user).orElse(null);
        return HabitsResponse.from(habit, log, streaks);
    }

    /**
     * 카운터 -1 (실수 취소)
     * 취소 정책 C안: 이미 발행된 챌린지/포인트 이벤트는 롤백하지 않음
     * 단, isAchieved는 재평가
     */
    @Transactional
    public HabitsResponse decrement(Long habitId, User user) {
        Habits habit = habitsRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new IllegalArgumentException("습관을 찾을 수 없습니다: " + habitId));

        LocalDate today = LocalDate.now();
        HabitLog log = habitLogRepository.findByHabitAndUserAndLogDate(habit, user, today)
                .orElseThrow(() -> new IllegalStateException("오늘 기록이 없습니다"));

        if (log.getCurrentCount() <= 0) {
            throw new IllegalStateException("카운터가 이미 0입니다");
        }

        log.setCurrentCount(log.getCurrentCount() - 1);
        // C안: isAchieved는 재평가하지 않음 (이벤트 롤백 없음)
        habitLogRepository.save(log);

        HabitStreaks streaks = habitStreaksRepository.findByHabitAndUser(habit, user).orElse(null);
        this.log.info("습관 카운터 감소 - habitId={}, userId={}, count={}", habitId, user.getId(), log.getCurrentCount());
        return HabitsResponse.from(habit, log, streaks);
    }

    // 스케줄러에서 호출 — 활성 습관 전체 조회
    public List<Habits> findAllActiveHabits() {
        return habitsRepository.findAll().stream()
                .filter(Habits::getIsActive)
                .collect(Collectors.toList());
    }

    public List<com.librarian.todo_list.habits.dto.HabitLogResponse> getHabitLogs(Long habitId, User user, java.time.LocalDate from, java.time.LocalDate to) {
        Habits habit = habitsRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new IllegalArgumentException("습관을 찾을 수 없습니다: " + habitId));
        return habitLogRepository.findByHabitAndLogDateBetweenOrderByLogDateAsc(habit, from, to)
                .stream()
                .map(com.librarian.todo_list.habits.dto.HabitLogResponse::from)
                .collect(Collectors.toList());
}
}
