package com.librarian.todo_list.goals.service;

import com.librarian.todo_list.goals.dto.GoalsRegistrationRequest;
import com.librarian.todo_list.goals.dto.GoalsResponse;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.goals.repository.GoalsRepository;
import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalsService {
    
    private final GoalsRepository goalsRepository;
    private final GoalProcessService goalProcessService;
    private final GoalStreaksService goalStreaksService;
    
    @Transactional
    public GoalsResponse createGoal(GoalsRegistrationRequest request, User user) {
        if (goalsRepository.existsByUserAndNameAndIsActiveTrue(user, request.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 활성 목표가 존재합니다: " + request.getName());
        }
        
        Goals goal = Goals.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .recurrenceType(request.getRecurrenceType())
                .interval(request.getInterval())
                .startDate(request.getStartDate())
                .targetCount(request.getTargetCount())
                .isActive(true)
                .build();
        
        Goals savedGoal = goalsRepository.save(goal);
        
        goalProcessService.initializeGoalProcess(savedGoal, user);
        goalStreaksService.initializeGoalStreaks(savedGoal, user);
        
        log.info("새 목표 생성 완료: goalId={}, name={}, user={}", 
                savedGoal.getId(), savedGoal.getName(), user.getEmail());
        
        return GoalsResponse.from(savedGoal);
    }
    
    public List<GoalsResponse> getUserGoals(User user) {
        List<Goals> goals = goalsRepository.findByUserAndIsActiveTrueOrderByCreatedAtDesc(user);
        return goals.stream()
                .map(GoalsResponse::from)
                .collect(Collectors.toList());
    }
    
    public GoalsResponse getGoal(Long goalId, User user) {
        Goals goal = goalsRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다: " + goalId));
        
        return GoalsResponse.from(goal);
    }
    
    @Transactional
    public GoalsResponse updateGoal(Long goalId, GoalsRegistrationRequest request, User user) {
        Goals goal = goalsRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다: " + goalId));
        
        if (!goal.getName().equals(request.getName()) && 
            goalsRepository.existsByUserAndNameAndIsActiveTrue(user, request.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 활성 목표가 존재합니다: " + request.getName());
        }
        
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setTargetCount(request.getTargetCount());
        
        Goals updatedGoal = goalsRepository.save(goal);
        
        log.info("목표 수정 완료: goalId={}, name={}", goalId, request.getName());
        
        return GoalsResponse.from(updatedGoal);
    }
    
    @Transactional
    public void deactivateGoal(Long goalId, User user) {
        Goals goal = goalsRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다: " + goalId));
        
        goal.setIsActive(false);
        goalsRepository.save(goal);
        
        goalProcessService.finalizeGoalProcess(goal, user);
        
        log.info("목표 비활성화 완료: goalId={}, name={}", goalId, goal.getName());
    }
    
    public Goals findByIdAndUser(Long goalId, User user) {
        return goalsRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다: " + goalId));
    }
    
    public List<Goals> findGoalsToReset(LocalDate currentDate) {
        return goalsRepository.findGoalsToReset(currentDate);
    }
}