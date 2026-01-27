package com.librarian.todo_list.goals.repository;

import com.librarian.todo_list.goals.entity.GoalProcess;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalProcessRepository extends JpaRepository<GoalProcess, Long> {
    
    Optional<GoalProcess> findByGoalsAndUserAndIsFinalizedFalse(Goals goals, User user);
    
    List<GoalProcess> findByUserAndIsFinalizedFalse(User user);
    
    List<GoalProcess> findByUserAndIsFinalizedFalseOrderByPeriodStartDesc(User user);
    
    @Query("SELECT gp FROM GoalProcess gp WHERE gp.periodEnd < :currentDate AND gp.isFinalized = false")
    List<GoalProcess> findExpiredPeriods(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT gp FROM GoalProcess gp WHERE gp.goals = :goals AND gp.user = :user " +
           "AND gp.periodStart <= :date AND gp.periodEnd >= :date")
    Optional<GoalProcess> findByGoalsAndUserAndPeriod(@Param("goals") Goals goals, 
                                                      @Param("user") User user, 
                                                      @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(gp) FROM GoalProcess gp WHERE gp.goals = :goals AND gp.user = :user " +
           "AND gp.isAchieved = true AND gp.isFinalized = true")
    Long countAchievedPeriods(@Param("goals") Goals goals, @Param("user") User user);
    
    @Query("SELECT gp FROM GoalProcess gp WHERE gp.goals = :goals AND gp.user = :user " +
           "AND gp.isFinalized = true ORDER BY gp.periodEnd DESC")
    List<GoalProcess> findFinalizedPeriodsOrderByEndDesc(@Param("goals") Goals goals, 
                                                         @Param("user") User user);
}