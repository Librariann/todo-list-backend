package com.librarian.todo_list.goals.service;

import com.librarian.todo_list.goals.entity.GoalProcess;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.goals.repository.GoalProcessRepository;
import com.librarian.todo_list.points.entity.UserPoint;
import com.librarian.todo_list.points.service.UserPointService;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalProcessService {
    
    private final GoalProcessRepository goalProcessRepository;
    private final GoalStreaksService goalStreaksService;
    private final UserPointService userPointService;
    
    @Transactional
    public void initializeGoalProcess(Goals goal, User user) {
        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = calculatePeriodEnd(startDate, goal.getRecurrenceType(), goal.getInterval());
        
        GoalProcess process = GoalProcess.builder()
                .goals(goal)
                .user(user)
                .periodIndex(1)
                .periodStart(startDate)
                .periodEnd(endDate)
                .currentCount(0)
                .isAchieved(false)
                .isFinalized(false)
                .build();
        
        goalProcessRepository.save(process);
        log.info("목표 프로세스 초기화 완료: goalId={}, period={} ~ {}", 
                goal.getId(), startDate, endDate);
    }
    
    @Transactional
    public boolean achieveGoal(Goals goal, User user) {
        GoalProcess process = goalProcessRepository.findByGoalsAndUserAndIsFinalizedFalse(goal, user)
                .orElseThrow(() -> new IllegalArgumentException("활성 목표 프로세스를 찾을 수 없습니다"));
        
        if (process.getIsAchieved()) {
            throw new IllegalStateException("이미 달성된 목표입니다");
        }
        
        process.setCurrentCount(process.getCurrentCount() + 1);
        
        boolean wasAchieved = process.getCurrentCount() >= goal.getTargetCount();
        if (wasAchieved && !process.getIsAchieved()) {
            process.setIsAchieved(true);
            
            grantAchievementReward(goal, user);
            
            log.info("목표 달성 완료: goalId={}, userId={}, count={}/{}", 
                    goal.getId(), user.getId(), process.getCurrentCount(), goal.getTargetCount());
        }
        
        goalProcessRepository.save(process);
        return wasAchieved;
    }
    
    @Transactional
    public void resetPeriod(Goals goal, User user) {
        GoalProcess currentProcess = goalProcessRepository.findByGoalsAndUserAndIsFinalizedFalse(goal, user)
                .orElse(null);
        
        if (currentProcess != null) {
            currentProcess.setIsFinalized(true);
            goalProcessRepository.save(currentProcess);
            
            goalStreaksService.updateStreak(goal, user, currentProcess.getIsAchieved());
        }
        
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = calculatePeriodEnd(newStartDate, goal.getRecurrenceType(), goal.getInterval());
        
        Integer nextPeriodIndex = currentProcess != null ? currentProcess.getPeriodIndex() + 1 : 1;
        
        GoalProcess newProcess = GoalProcess.builder()
                .goals(goal)
                .user(user)
                .periodIndex(nextPeriodIndex)
                .periodStart(newStartDate)
                .periodEnd(newEndDate)
                .currentCount(0)
                .isAchieved(false)
                .isFinalized(false)
                .build();
        
        goalProcessRepository.save(newProcess);
        
        log.info("목표 주기 초기화 완료: goalId={}, period={}, start={}, end={}", 
                goal.getId(), nextPeriodIndex, newStartDate, newEndDate);
    }
    
    @Transactional
    public void finalizeGoalProcess(Goals goal, User user) {
        goalProcessRepository.findByGoalsAndUserAndIsFinalizedFalse(goal, user)
                .ifPresent(process -> {
                    process.setIsFinalized(true);
                    goalProcessRepository.save(process);
                    
                    goalStreaksService.updateStreak(goal, user, process.getIsAchieved());
                });
    }
    
    public GoalProcess getCurrentProcess(Goals goal, User user) {
        return goalProcessRepository.findByGoalsAndUserAndIsFinalizedFalse(goal, user)
                .orElse(null);
    }
    
    public List<GoalProcess> getExpiredPeriods(LocalDate currentDate) {
        return goalProcessRepository.findExpiredPeriods(currentDate);
    }
    
    public List<GoalProcess> getUserActiveProcesses(User user) {
        return goalProcessRepository.findByUserAndIsFinalizedFalseOrderByPeriodStartDesc(user);
    }
    
    private LocalDate calculatePeriodEnd(LocalDate startDate, UserPoint.PeriodTypeStatus recurrenceType, Integer interval) {
        return switch (recurrenceType) {
            case DAILY -> startDate.plusDays(interval - 1);
            case WEEKLY -> startDate.plusWeeks(interval).minusDays(1);
            case MONTHLY -> startDate.plusMonths(interval).minusDays(1);
        };
    }
    
    private void grantAchievementReward(Goals goal, User user) {
        int basePoints = switch (goal.getRecurrenceType()) {
            case DAILY -> 10;
            case WEEKLY -> 50;
            case MONTHLY -> 200;
        };
        
        int rewardPoints = basePoints * goal.getInterval();
        
        String periodKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        userPointService.awardChallengePoints(
            user, 
            rewardPoints,
            goal.getId(),
            goal.getRecurrenceType()
        );
        
        log.info("목표 달성 포인트 지급: goalId={}, userId={}, points={}", 
                goal.getId(), user.getId(), rewardPoints);
    }
}