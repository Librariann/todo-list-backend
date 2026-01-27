package com.librarian.todo_list.goals.repository;

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
public interface GoalsRepository extends JpaRepository<Goals, Long> {
    
    List<Goals> findByUserAndIsActiveTrue(User user);
    
    List<Goals> findByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    
    Optional<Goals> findByIdAndUser(Long id, User user);
    
    @Query("SELECT g FROM Goals g WHERE g.isActive = true AND " +
           "((g.recurrenceType = 'DAILY' AND DATEDIFF(:currentDate, g.startDate) % g.interval = 0) OR " +
           "(g.recurrenceType = 'WEEKLY' AND DATEDIFF(:currentDate, g.startDate) % (g.interval * 7) = 0) OR " +
           "(g.recurrenceType = 'MONTHLY' AND FUNCTION('DAY', :currentDate) = FUNCTION('DAY', g.startDate) AND " +
           "DATEDIFF(MONTH, g.startDate, :currentDate) % g.interval = 0))")
    List<Goals> findGoalsToReset(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT COUNT(g) > 0 FROM Goals g WHERE g.user = :user AND g.name = :name AND g.isActive = true")
    boolean existsByUserAndNameAndIsActiveTrue(@Param("user") User user, @Param("name") String name);
}