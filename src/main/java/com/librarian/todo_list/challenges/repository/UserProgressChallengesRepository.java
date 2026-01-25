package com.librarian.todo_list.challenges.repository;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressChallengesRepository extends JpaRepository<UserProgressChallenges, Long> {
    
    Optional<UserProgressChallenges> findByUserAndChallengesAndPeriodTypeAndPeriodKey(
        User user, Challenges challenges, UserPoint.PeriodTypeStatus periodType, String periodKey);
        
    List<UserProgressChallenges> findByUserAndPeriodTypeAndPeriodKey(
        User user, UserPoint.PeriodTypeStatus periodType, String periodKey);
}
