package com.librarian.todo_list.challenges.controller;

import com.librarian.todo_list.challenges.dto.*;
import com.librarian.todo_list.challenges.service.ChallengesService;
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

    @Operation(
        summary = "도전과제 진행상황 업데이트", 
        description = "사용자의 도전과제 진행상황을 수동으로 업데이트합니다. 일반적으로는 할 일 완료 시 자동으로 처리되지만, 수동 업데이트가 필요한 경우 사용합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "진행상황 업데이트 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 데이터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "도전과제를 찾을 수 없음"
        )
    })
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
