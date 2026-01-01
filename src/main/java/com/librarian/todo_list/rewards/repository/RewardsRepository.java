package com.librarian.todo_list.rewards.repository;

import com.librarian.todo_list.rewards.entity.Rewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
    List<Rewards> findByIsActiveTrue();
    Optional<Rewards> findByIdAndIsActiveTrue(Long id);
}
