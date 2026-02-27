package com.librarian.todo_list.points.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.points.dto.UserPointsInputRequest;
import com.librarian.todo_list.points.service.UserPointService;
import com.librarian.todo_list.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트 관리", description = "유저 포인트 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/user/points")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserPointController {

    private final UserPointService userPointService;

    @Operation(summary = "유저 포인트 조회", description = "유저의 포인트를 조회합니다.")
    @GetMapping("/")
    public ResponseEntity<ApiResponse<Integer>> getUserPoints(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("유저 포인트 조회 API 호출: userId={}", principal.getUser().getId());
        Integer points = userPointService.getUserTotalPoints(principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(points, "포인트 조회 성공"));
    }
    @Operation(summary = "유저 포인트 입력 (어드민 전용)", description = "어드민이 특정 사용자의 포인트를 지급합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<ApiResponse<Integer>> inputUserPoints(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody UserPointsInputRequest request) {
        log.info("유저 포인트 입력 API 호출: adminId={}, targetUserId={}, point={}",
                principal.getUser().getId(), request.getId(), request.getPoint());
        Integer totalPoints = userPointService.inputUserPoints(principal.getUser(), request);
        return ResponseEntity.ok(ApiResponse.success(totalPoints, "포인트 입력 성공"));
    }
}
