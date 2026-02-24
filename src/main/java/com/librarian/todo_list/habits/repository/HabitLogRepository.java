package com.librarian.todo_list.habits.repository;

import com.librarian.todo_list.habits.entity.HabitLog;
import com.librarian.todo_list.habits.entity.Habits;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    Optional<HabitLog> findByHabitAndLogDate(Habits habit, LocalDate logDate);

    List<HabitLog> findByUserAndLogDate(User user, LocalDate logDate);

    // 자정 리셋 스케줄러용: 어제 달성 여부 확인
    Optional<HabitLog> findByHabitAndUserAndLogDate(Habits habit, User user, LocalDate logDate);

    // 잔디용: 특정 기간 로그 조회
    List<HabitLog> findByHabitAndLogDateBetweenOrderByLogDateAsc(Habits habit, LocalDate from, LocalDate to);
}
