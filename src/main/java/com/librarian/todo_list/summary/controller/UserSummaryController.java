package com.librarian.todo_list.summary.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.security.CustomUserDetails;
import com.librarian.todo_list.summary.dto.UserSummaryResponse;
import com.librarian.todo_list.summary.service.UserSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 요약", description = "사용자 주요 데이터 통합 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/user/summary")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserSummaryController {

    private final UserSummaryService userSummaryService;

    @Operation(summary = "사용자 요약 조회", description = "현재 사용자의 보상, 달성한 도전과제, 포인트를 한번에 조회합니다.")
    @GetMapping("/")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getUserSummary(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("유저 요약 조회 API 호출: userId={}", principal.getUser().getId());

        UserSummaryResponse response = userSummaryService.getUserSummary(principal.getUser());

        return ResponseEntity.ok(ApiResponse.success(response, "사용자 요약 정보를 성공적으로 불러왔습니다."));
    }
}
