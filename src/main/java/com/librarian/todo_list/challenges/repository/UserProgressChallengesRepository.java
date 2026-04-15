package com.librarian.todo_list.challenges.repository;

import com.librarian.todo_list.challenges.dto.ChallengesWithProgressResponse;
import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressChallengesRepository extends JpaRepository<UserProgressChallenges, Long> {
    
    Optional<UserProgressChallenges> findByUserAndChallengesAndPeriodTypeAndPeriodKey(
        User user, Challenges challenges, UserPoint.PeriodTypeStatus periodType, String periodKey);
        
    List<UserProgressChallenges> findByUserAndPeriodTypeAndPeriodKey(
        User user, UserPoint.PeriodTypeStatus periodType, String periodKey);

    List<UserProgressChallenges> findByUserAndIsAchievedTrue(User user);

    List<UserProgressChallenges> findByUser(User user);

    @Query("SELECT upc FROM UserProgressChallenges upc " +
            "WHERE upc.user = :user " +
            "AND (" +
            "  (upc.periodType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.DAILY   AND upc.periodKey = :dailyKey) OR " +
            "  (upc.periodType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.WEEKLY  AND upc.periodKey = :weeklyKey) OR " +
            "  (upc.periodType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.MONTHLY AND upc.periodKey = :monthlyKey)" +
            ")")
    List<UserProgressChallenges> findCurrentPeriodProgressByUser(
            @Param("user") User user,
            @Param("dailyKey") String dailyKey,
            @Param("weeklyKey") String weeklyKey,
            @Param("monthlyKey") String monthlyKey);


    @Query("SELECT new com.librarian.todo_list.challenges.dto.ChallengesWithProgressResponse(" +
            "c.id, c.createdAt, c.updatedAt, c.name, c.description, c.icon, c.recurrenceType, c.workType, c.targetCount, c.point, " +
            "COALESCE(upc.currentCount, 0), COALESCE(upc.isAchieved, false), upc.periodKey) " +
            "FROM Challenges c " +
            "LEFT JOIN UserProgressChallenges upc ON upc.challenges = c " +
            "AND upc.user = :user " +
            "AND ((c.recurrenceType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.DAILY " +
            "      AND upc.periodType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.DAILY " +
            "      AND upc.periodKey = :dailyKey) " +
            "  OR (c.recurrenceType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.WEEKLY " +
            "      AND upc.periodType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.WEEKLY " +
            "      AND upc.periodKey = :weeklyKey) " +
            "  OR (c.recurrenceType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.MONTHLY " +
            "      AND upc.periodType = com.librarian.todo_list.points.entity.UserPoint$PeriodTypeStatus.MONTHLY " +
            "      AND upc.periodKey = :monthlyKey)) " +
            "WHERE c.isActive = true")
    List<ChallengesWithProgressResponse> findUserMatchChallengeByUser(
            @Param("user") User user,
            @Param("dailyKey") String dailyKey,
            @Param("weeklyKey") String weeklyKey,
            @Param("monthlyKey") String monthlyKey);
}
