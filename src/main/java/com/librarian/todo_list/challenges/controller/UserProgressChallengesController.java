package com.librarian.todo_list.challenges.controller;

import com.librarian.todo_list.challenges.dto.*;
import com.librarian.todo_list.challenges.service.UserProgressChallengesService;
import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

/**
 * 사용자 도전과제 진행상황 관련 REST API 컨트롤러
 */
@Tag(name = "사용자 도전과제 진행", description = "사용자의 도전과제 진행상황 업데이트 API")
@Slf4j
@RestController
@RequestMapping("/api/user/challenges")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserProgressChallengesController {
    private final UserProgressChallengesService userProgressChallengesService;

    @Operation(summary = "도전과제 목록 + 내 진행상황 조회", description = "활성화된 전체 도전과제 목록과 현재 로그인한 사용자의 이번 기간 진행상황을 함께 반환합니다.")
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ChallengesWithProgressResponse>>> getChallengesWithProgress(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("챌린지 + 진행상황 조회 API 호출: userId={}", principal.getUser().getId());

        List<ChallengesWithProgressResponse> response = userProgressChallengesService.getChallengesWithProgress(principal.getUser());

        return ResponseEntity.ok(ApiResponse.success(response, "도전과제 목록을 성공적으로 불러왔습니다."));
    }

    @Operation(summary = "달성한 도전과제 목록 조회", description = "현재 로그인한 사용자가 달성한 도전과제 목록을 조회합니다.")
    @GetMapping("/achieved")
    public ResponseEntity<ApiResponse<List<UserProgressChallengesRes>>> getAchievedChallenges(
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("달성한 도전과제 목록 조회 API 호출: userId={}", principal.getUser().getId());

        List<UserProgressChallengesRes> response = userProgressChallengesService.getAchievedChallenges(principal.getUser());

        return ResponseEntity.ok(ApiResponse.success(response, "달성한 도전과제 목록을 성공적으로 불러왔습니다."));
    }

    @Operation(
            summary = "도전과제 진행상황 업데이트",
            description = "사용자의 도전과제 진행상황을 수동으로 업데이트합니다. 일반적으로는 할 일 완료 시 자동으로 처리되지만, 수동 업데이트가 필요한 경우 사용합니다."
    )
    @PostMapping("/progress")
    public ResponseEntity<ApiResponse<UserProgressChallengesRes>> progressUserChallenge(
            @Valid @RequestBody UserProgressChallengesReq request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("도전과제 등록 API 호출: name={}", request.getName());

        UserProgressChallengesRes challengesResponse = userProgressChallengesService.progressChallenges(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(challengesResponse, "도전과제가 성공적으로 등록 완료되었습니다."));
    }


}
