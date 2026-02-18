package com.librarian.todo_list.habits.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.habits.dto.HabitLogResponse;
import com.librarian.todo_list.habits.dto.HabitsRegistrationRequest;
import com.librarian.todo_list.habits.dto.HabitsResponse;
import com.librarian.todo_list.habits.dto.HabitsUpdateRequest;

import com.librarian.todo_list.habits.service.HabitsService;
import com.librarian.todo_list.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "습관 관리", description = "습관 CRUD 및 카운터 API")
@Slf4j
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class HabitsController {

    private final HabitsService habitsService;

    @Operation(summary = "습관 목록 조회", description = "오늘 카운터 및 스트릭 정보를 포함한 활성 습관 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<HabitsResponse>>> getHabits(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("습관 목록 API 호출: userId={}", principal.getUser().getId());
        List<HabitsResponse> habits = habitsService.getUserHabits(principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(habits, "습관 목록 조회 성공"));
    }

    @Operation(summary = "습관 생성", description = "새 습관을 생성합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<HabitsResponse>> createHabit(
            @Valid @RequestBody HabitsRegistrationRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("습관 생성 API 호출: name={}, userId={}", request.getName(), principal.getUser().getId());
        HabitsResponse habit = habitsService.createHabit(request, principal.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(habit, "습관 생성 성공"));
    }

    @Operation(summary = "습관 수정", description = "습관 정보를 수정합니다.")
    @PatchMapping("/{habitId}")
    public ResponseEntity<ApiResponse<HabitsResponse>> updateHabit(
            @Parameter(description = "습관 ID") @PathVariable Long habitId,
            @Valid @RequestBody HabitsUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("습관 수정 API 호출: habitId={}, userId={}", habitId, principal.getUser().getId());
        HabitsResponse habit = habitsService.updateHabit(habitId, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(habit, "습관 수정 성공"));
    }

    @Operation(summary = "습관 삭제(비활성화)", description = "습관을 비활성화합니다.")
    @DeleteMapping("/{habitId}")
    public ResponseEntity<ApiResponse<Void>> deactivateHabit(
            @Parameter(description = "습관 ID") @PathVariable Long habitId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("습관 비활성화 API 호출: habitId={}, userId={}", habitId, principal.getUser().getId());
        habitsService.deactivateHabit(habitId, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(null, "습관 비활성화 성공"));
    }

    @Operation(summary = "카운터 +1", description = "오늘 카운터를 1 증가시킵니다. dailyTarget 달성 시 챌린지 이벤트가 발행됩니다.")
    @PostMapping("/{habitId}/increment")
    public ResponseEntity<ApiResponse<HabitsResponse>> increment(
            @Parameter(description = "습관 ID") @PathVariable Long habitId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("카운터 증가 API 호출: habitId={}, userId={}", habitId, principal.getUser().getId());
        HabitsResponse result = habitsService.increment(habitId, principal.getUser());
        String message = Boolean.TRUE.equals(result.getTodayAchieved()) ? "목표 달성!" : "카운터 증가 성공";
        return ResponseEntity.ok(ApiResponse.success(result, message));
    }

    @Operation(summary = "카운터 -1", description = "오늘 카운터를 1 감소시킵니다. 이미 지급된 포인트/챌린지는 롤백되지 않습니다.")
    @PostMapping("/{habitId}/decrement")
    public ResponseEntity<ApiResponse<HabitsResponse>> decrement(
            @Parameter(description = "습관 ID") @PathVariable Long habitId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("카운터 감소 API 호출: habitId={}, userId={}", habitId, principal.getUser().getId());
        HabitsResponse result = habitsService.decrement(habitId, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(result, "카운터 감소 성공"));
    }

    @Operation(summary = "습관 로그 조회 (잔디)", description = "특정 기간의 일별 달성 이력을 조회합니다.")
    @GetMapping("/{habitId}/logs")
    public ResponseEntity<ApiResponse<List<HabitLogResponse>>> getLogs(
            @Parameter(description = "습관 ID") @PathVariable Long habitId,
            @Parameter(description = "조회 시작일 (yyyy-MM-dd)") @RequestParam LocalDate from,
            @Parameter(description = "조회 종료일 (yyyy-MM-dd)") @RequestParam LocalDate to,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("습관 로그 조회 API 호출: habitId={}, from={}, to={}", habitId, from, to);

        List<HabitLogResponse> response = habitsService.getHabitLogs(habitId, principal.getUser(), from, to);
        return ResponseEntity.ok(ApiResponse.success(response, "습관 로그 조회 성공"));
    }
}
