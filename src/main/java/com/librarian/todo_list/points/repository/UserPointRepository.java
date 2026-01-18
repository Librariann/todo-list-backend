package com.librarian.todo_list.points.repository;

import com.librarian.todo_list.points.entity.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    @Query("SELECT SUM(CASE WHEN up.action = com.librarian.todo_list.points.entity.UserPoint$ActionStatus.CREDIT THEN up.point ELSE -up.point END) " +
           "FROM UserPoint up WHERE up.user.id = :userId")
    Optional<Integer> calculateUserTotalPoints(@Param("userId") Long userId);

    @Query("SELECT SUM(CASE WHEN up.action = com.librarian.todo_list.points.entity.UserPoint$ActionStatus.CREDIT THEN up.point ELSE -up.point END) " +
           "FROM UserPoint up WHERE up.user.id = :userId " +
           "AND up.periodType = :periodType AND up.periodKey = :periodKey")
    Optional<Integer> findPointsByUserAndPeriod(
        @Param("userId") Long userId,
        @Param("periodType") UserPoint.PeriodTypeStatus periodType,
        @Param("periodKey") String periodKey);
}