package com.librarian.todo_list.habits.repository;

import com.librarian.todo_list.habits.entity.Habits;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitsRepository extends JpaRepository<Habits, Long> {

    List<Habits> findByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);

    Optional<Habits> findByIdAndUser(Long id, User user);

    boolean existsByUserAndNameAndIsActiveTrue(User user, String name);
}
