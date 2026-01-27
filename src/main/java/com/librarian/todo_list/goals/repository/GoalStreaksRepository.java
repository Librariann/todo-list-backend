package com.librarian.todo_list.goals.repository;

import com.librarian.todo_list.goals.entity.GoalStreaks;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalStreaksRepository extends JpaRepository<GoalStreaks, Long> {
    
    Optional<GoalStreaks> findByGoalsAndUser(Goals goals, User user);
    
    List<GoalStreaks> findByUser(User user);
    
    List<GoalStreaks> findByUserOrderByCurrentStreakDesc(User user);
    
    @Query("SELECT gs FROM GoalStreaks gs WHERE gs.user = :user AND gs.currentStreak > 0 " +
           "ORDER BY gs.currentStreak DESC")
    List<GoalStreaks> findActiveStreaksByUser(@Param("user") User user);
    
    @Query("SELECT gs FROM GoalStreaks gs WHERE gs.user = :user AND gs.longestStreak > 0 " +
           "ORDER BY gs.longestStreak DESC")
    List<GoalStreaks> findBestStreaksByUser(@Param("user") User user);
    
    @Query("SELECT MAX(gs.longestStreak) FROM GoalStreaks gs WHERE gs.user = :user")
    Integer findMaxStreakByUser(@Param("user") User user);
    
    @Query("SELECT SUM(gs.currentStreak) FROM GoalStreaks gs WHERE gs.user = :user AND gs.currentStreak > 0")
    Integer findTotalActiveStreaksByUser(@Param("user") User user);
}