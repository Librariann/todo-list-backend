package com.librarian.todo_list.goals.service;

import com.librarian.todo_list.goals.entity.GoalProcess;
import com.librarian.todo_list.goals.entity.Goals;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalSchedulerService {
    
    private final GoalProcessService goalProcessService;
    private final GoalsService goalsService;
    
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processExpiredGoalPeriods() {
        LocalDate currentDate = LocalDate.now();
        log.info("목표 주기 만료 처리 시작: date={}", currentDate);
        
        try {
            List<GoalProcess> expiredProcesses = goalProcessService.getExpiredPeriods(currentDate);
            log.info("만료된 목표 프로세스 발견: count={}", expiredProcesses.size());
            
            for (GoalProcess process : expiredProcesses) {
                try {
                    Goals goal = process.getGoals();
                    if (goal.getIsActive()) {
                        goalProcessService.resetPeriod(goal, process.getUser());
                        log.debug("목표 주기 초기화 완료: goalId={}, userId={}, periodIndex={}", 
                                goal.getId(), process.getUser().getId(), process.getPeriodIndex());
                    } else {
                        process.setIsFinalized(true);
                        log.debug("비활성 목표 프로세스 최종화: goalId={}, userId={}", 
                                goal.getId(), process.getUser().getId());
                    }
                } catch (Exception e) {
                    log.error("목표 프로세스 처리 중 오류: processId={}, error={}", 
                            process.getId(), e.getMessage(), e);
                }
            }
            
            log.info("목표 주기 만료 처리 완료: processed={}", expiredProcesses.size());
            
        } catch (Exception e) {
            log.error("목표 주기 만료 처리 중 전체 오류 발생", e);
        }
    }
    
    @Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    public void processIntervalBasedGoalResets() {
        LocalDate currentDate = LocalDate.now();
        log.info("간격 기반 목표 초기화 처리 시작: date={}", currentDate);
        
        try {
            List<Goals> goalsToReset = goalsService.findGoalsToReset(currentDate);
            log.info("간격 기반 초기화 대상 목표 발견: count={}", goalsToReset.size());
            
            for (Goals goal : goalsToReset) {
                try {
                    if (goal.getIsActive()) {
                        goalProcessService.resetPeriod(goal, goal.getUser());
                        log.debug("간격 기반 목표 초기화 완료: goalId={}, userId={}, interval={}, type={}", 
                                goal.getId(), goal.getUser().getId(), goal.getInterval(), goal.getRecurrenceType());
                    }
                } catch (Exception e) {
                    log.error("간격 기반 목표 처리 중 오류: goalId={}, error={}", 
                            goal.getId(), e.getMessage(), e);
                }
            }
            
            log.info("간격 기반 목표 초기화 처리 완료: processed={}", goalsToReset.size());
            
        } catch (Exception e) {
            log.error("간격 기반 목표 초기화 처리 중 전체 오류 발생", e);
        }
    }
    
    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    public void weeklyGoalMaintenanceTask() {
        log.info("주간 목표 유지보수 작업 시작");
        
        try {
            log.info("주간 목표 유지보수 작업 완료");
            
        } catch (Exception e) {
            log.error("주간 목표 유지보수 작업 중 오류 발생", e);
        }
    }
    
    @Scheduled(cron = "0 0 3 1 * ?")
    @Transactional  
    public void monthlyGoalMaintenanceTask() {
        log.info("월간 목표 유지보수 작업 시작");
        
        try {
            log.info("월간 목표 유지보수 작업 완료");
            
        } catch (Exception e) {
            log.error("월간 목표 유지보수 작업 중 오류 발생", e);
        }
    }
}