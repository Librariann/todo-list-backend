package com.librarian.todo_list.todos.repository;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.todos.entity.Todos;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodosRepository extends JpaRepository<Todos, Long> {
    boolean existsByName(String name);
    Optional<Todos> findTopByUserAndTargetDateOrderByOrderIndexDesc(User user, LocalDate targetDate);
    boolean existsByNameAndIdNot(String name, Long id);
    Optional<Todos> findByIdAndUserId(Long id, Long userId);
}
