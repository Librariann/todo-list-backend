package com.librarian.todo_list.goals.service;

import com.librarian.todo_list.goals.entity.GoalStreaks;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.goals.repository.GoalStreaksRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalStreaksService {
    
    private final GoalStreaksRepository goalStreaksRepository;
    
    @Transactional
    public void initializeGoalStreaks(Goals goal, User user) {
        GoalStreaks streaks = GoalStreaks.builder()
                .goals(goal)
                .user(user)
                .currentStreak(0)
                .longestStreak(0)
                .build();
        
        goalStreaksRepository.save(streaks);
        log.info("목표 스트릭 초기화 완료: goalId={}, userId={}", goal.getId(), user.getId());
    }
    
    @Transactional
    public void updateStreak(Goals goal, User user, boolean isAchieved) {
        GoalStreaks streaks = goalStreaksRepository.findByGoalsAndUser(goal, user)
                .orElseThrow(() -> new IllegalArgumentException("목표 스트릭을 찾을 수 없습니다"));
        
        if (isAchieved) {
            streaks.setCurrentStreak(streaks.getCurrentStreak() + 1);
            
            if (streaks.getCurrentStreak() > streaks.getLongestStreak()) {
                streaks.setLongestStreak(streaks.getCurrentStreak());
                log.info("최장 스트릭 갱신: goalId={}, userId={}, newRecord={}", 
                        goal.getId(), user.getId(), streaks.getLongestStreak());
            }
        } else {
            if (streaks.getCurrentStreak() > 0) {
                log.info("스트릭 중단: goalId={}, userId={}, previousStreak={}", 
                        goal.getId(), user.getId(), streaks.getCurrentStreak());
            }
            streaks.setCurrentStreak(0);
        }
        
        goalStreaksRepository.save(streaks);
        log.debug("스트릭 업데이트 완료: goalId={}, userId={}, current={}, longest={}", 
                goal.getId(), user.getId(), streaks.getCurrentStreak(), streaks.getLongestStreak());
    }
    
    public GoalStreaks getGoalStreaks(Goals goal, User user) {
        return goalStreaksRepository.findByGoalsAndUser(goal, user)
                .orElse(null);
    }
    
    public List<GoalStreaks> getUserStreaks(User user) {
        return goalStreaksRepository.findByUser(user);
    }
    
    public List<GoalStreaks> getUserActiveStreaks(User user) {
        return goalStreaksRepository.findActiveStreaksByUser(user);
    }
    
    public List<GoalStreaks> getUserBestStreaks(User user) {
        return goalStreaksRepository.findBestStreaksByUser(user);
    }
    
    public Integer getUserMaxStreak(User user) {
        return goalStreaksRepository.findMaxStreakByUser(user);
    }
    
    public Integer getUserTotalActiveStreaks(User user) {
        return goalStreaksRepository.findTotalActiveStreaksByUser(user);
    }
    
    @Transactional
    public void resetStreak(Goals goal, User user) {
        goalStreaksRepository.findByGoalsAndUser(goal, user)
                .ifPresent(streaks -> {
                    streaks.setCurrentStreak(0);
                    goalStreaksRepository.save(streaks);
                    log.info("스트릭 초기화 완료: goalId={}, userId={}", goal.getId(), user.getId());
                });
    }
}