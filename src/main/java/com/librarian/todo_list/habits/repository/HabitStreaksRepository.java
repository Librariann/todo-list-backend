package com.librarian.todo_list.habits.repository;

import com.librarian.todo_list.habits.entity.HabitStreaks;
import com.librarian.todo_list.habits.entity.Habits;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HabitStreaksRepository extends JpaRepository<HabitStreaks, Long> {

    Optional<HabitStreaks> findByHabitAndUser(Habits habit, User user);
}
