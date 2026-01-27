package com.librarian.todo_list.goals.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.goals.dto.*;
import com.librarian.todo_list.goals.entity.GoalProcess;
import com.librarian.todo_list.goals.entity.GoalStreaks;
import com.librarian.todo_list.goals.entity.Goals;
import com.librarian.todo_list.goals.service.GoalProcessService;
import com.librarian.todo_list.goals.service.GoalStreaksService;
import com.librarian.todo_list.goals.service.GoalsService;
import com.librarian.todo_list.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "목표 관리", description = "목표 생성, 조회, 달성, 스트릭 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GoalsController {
    
    private final GoalsService goalsService;
    private final GoalProcessService goalProcessService;
    private final GoalStreaksService goalStreaksService;
    
    @Operation(summary = "사용자 목표 대시보드", description = "사용자의 모든 활성 목표와 스트릭 정보를 조회합니다.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<GoalDashboardResponse>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 대시보드 API 호출: userId={}", principal.getUser().getId());
        
        List<GoalProcess> activeProcesses = goalProcessService.getUserActiveProcesses(principal.getUser());
        List<GoalStreaks> userStreaks = goalStreaksService.getUserStreaks(principal.getUser());
        
        List<GoalProcessResponse> activeGoals = activeProcesses.stream()
                .map(GoalProcessResponse::from)
                .collect(Collectors.toList());
        
        List<GoalStreaksResponse> activeStreaks = userStreaks.stream()
                .map(GoalStreaksResponse::from)
                .collect(Collectors.toList());
        
        GoalDashboardResponse.GoalStatsResponse stats = GoalDashboardResponse.GoalStatsResponse.builder()
                .totalActiveGoals(activeGoals.size())
                .totalAchievedToday((int) activeGoals.stream().filter(GoalProcessResponse::getIsAchieved).count())
                .totalActiveStreaks((int) activeStreaks.stream().filter(GoalStreaksResponse::getIsActive).count())
                .longestCurrentStreak(activeStreaks.stream().mapToInt(GoalStreaksResponse::getCurrentStreak).max().orElse(0))
                .maxStreakEver(goalStreaksService.getUserMaxStreak(principal.getUser()))
                .totalStreaksActive(goalStreaksService.getUserTotalActiveStreaks(principal.getUser()))
                .build();
        
        GoalDashboardResponse dashboard = GoalDashboardResponse.builder()
                .activeGoals(activeGoals)
                .activeStreaks(activeStreaks)
                .stats(stats)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(dashboard, "대시보드 조회 성공"));
    }
    
    @Operation(summary = "목표 목록 조회", description = "사용자의 모든 활성 목표를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalsResponse>>> getGoals(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 목록 API 호출: userId={}", principal.getUser().getId());
        
        List<GoalsResponse> goals = goalsService.getUserGoals(principal.getUser());
        
        return ResponseEntity.ok(ApiResponse.success(goals, "목표 목록 조회 성공"));
    }
    
    @Operation(summary = "목표 상세 조회", description = "특정 목표의 상세 정보를 조회합니다.")
    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<GoalsResponse>> getGoal(
            @Parameter(description = "목표 ID", example = "1")
            @PathVariable Long goalId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 상세 조회 API 호출: goalId={}, userId={}", goalId, principal.getUser().getId());
        
        GoalsResponse goal = goalsService.getGoal(goalId, principal.getUser());
        
        return ResponseEntity.ok(ApiResponse.success(goal, "목표 상세 조회 성공"));
    }
    
    @Operation(summary = "새 목표 생성", description = "새로운 목표를 생성합니다. 생성과 동시에 초기 프로세스와 스트릭이 설정됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<GoalsResponse>> createGoal(
            @Valid @RequestBody GoalsRegistrationRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 생성 API 호출: name={}, userId={}", request.getName(), principal.getUser().getId());
        
        GoalsResponse goal = goalsService.createGoal(request, principal.getUser());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(goal, "목표 생성 성공"));
    }
    
    @Operation(summary = "목표 수정", description = "기존 목표의 정보를 수정합니다.")
    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse<GoalsResponse>> updateGoal(
            @Parameter(description = "목표 ID", example = "1")
            @PathVariable Long goalId,
            @Valid @RequestBody GoalsRegistrationRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 수정 API 호출: goalId={}, name={}, userId={}", 
                goalId, request.getName(), principal.getUser().getId());
        
        GoalsResponse goal = goalsService.updateGoal(goalId, request, principal.getUser());
        
        return ResponseEntity.ok(ApiResponse.success(goal, "목표 수정 성공"));
    }
    
    @Operation(summary = "목표 달성", description = "목표를 달성 처리합니다. 달성 시 포인트 지급 및 스트릭이 업데이트됩니다.")
    @PostMapping("/{goalId}/achieve")
    public ResponseEntity<ApiResponse<GoalProcessResponse>> achieveGoal(
            @Parameter(description = "목표 ID", example = "1")
            @PathVariable Long goalId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 달성 API 호출: goalId={}, userId={}", goalId, principal.getUser().getId());
        
        Goals goal = goalsService.findByIdAndUser(goalId, principal.getUser());
        boolean wasAchieved = goalProcessService.achieveGoal(goal, principal.getUser());
        GoalProcess process = goalProcessService.getCurrentProcess(goal, principal.getUser());
        
        String message = wasAchieved ? "목표 달성 완료! 포인트가 지급되었습니다." : "목표 진행도가 업데이트되었습니다.";
        
        return ResponseEntity.ok(ApiResponse.success(GoalProcessResponse.from(process), message));
    }
    
    @Operation(summary = "목표 비활성화", description = "목표를 비활성화합니다. 비활성화된 목표는 더 이상 진행되지 않습니다.")
    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deactivateGoal(
            @Parameter(description = "목표 ID", example = "1")
            @PathVariable Long goalId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 비활성화 API 호출: goalId={}, userId={}", goalId, principal.getUser().getId());
        
        goalsService.deactivateGoal(goalId, principal.getUser());
        
        return ResponseEntity.ok(ApiResponse.success(null, "목표 비활성화 성공"));
    }
    
    @Operation(summary = "목표 진행상황 조회", description = "특정 목표의 현재 진행상황을 조회합니다.")
    @GetMapping("/{goalId}/progress")
    public ResponseEntity<ApiResponse<GoalProcessResponse>> getGoalProgress(
            @Parameter(description = "목표 ID", example = "1")
            @PathVariable Long goalId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 진행상황 조회 API 호출: goalId={}, userId={}", goalId, principal.getUser().getId());
        
        Goals goal = goalsService.findByIdAndUser(goalId, principal.getUser());
        GoalProcess process = goalProcessService.getCurrentProcess(goal, principal.getUser());
        
        return ResponseEntity.ok(ApiResponse.success(GoalProcessResponse.from(process), "진행상황 조회 성공"));
    }
    
    @Operation(summary = "목표 스트릭 조회", description = "특정 목표의 스트릭 정보를 조회합니다.")
    @GetMapping("/{goalId}/streaks")
    public ResponseEntity<ApiResponse<GoalStreaksResponse>> getGoalStreaks(
            @Parameter(description = "목표 ID", example = "1")
            @PathVariable Long goalId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("목표 스트릭 조회 API 호출: goalId={}, userId={}", goalId, principal.getUser().getId());
        
        Goals goal = goalsService.findByIdAndUser(goalId, principal.getUser());
        GoalStreaks streaks = goalStreaksService.getGoalStreaks(goal, principal.getUser());
        
        return ResponseEntity.ok(ApiResponse.success(GoalStreaksResponse.from(streaks), "스트릭 조회 성공"));
    }
}
