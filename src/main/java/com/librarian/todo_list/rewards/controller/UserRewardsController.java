package com.librarian.todo_list.rewards.controller;

import com.librarian.todo_list.challenges.controller.UserProgressChallengesController;
import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.rewards.dto.*;
import com.librarian.todo_list.rewards.service.RewardsService;
import com.librarian.todo_list.rewards.service.UserRewardsService;
import com.librarian.todo_list.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * 사용자 보상 관련 REST API 컨트롤러
 */
@Tag(name = "사용자 보상", description = "사용자의 보상 받기, 사용하기, 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/user/rewards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserRewardsController {
    private final UserRewardsService userRewardsService;

    @Operation(
        summary = "내가 받은 보상 목록 조회", 
        description = "현재 사용자가 받은 모든 보상 목록을 조회합니다."
    )
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<UserRewardsResponse>>> getUserRewardList() {
        log.info("유저 상품 목록 API 호출");

        List<UserRewardsResponse> rewardResponse = userRewardsService.getUserRewards();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "받은 보상 목록을 성공적으로 불러왔습니다."));
    }

    @Operation(
        summary = "보상 받기", 
        description = "포인트를 사용하여 보상을 받습니다. 충분한 포인트가 있어야 합니다."
    )
    @PostMapping("/{rewardId}/redeem")
    public ResponseEntity<ApiResponse<UserRewardsResponse>> redeemUserReward(
            @Parameter(description = "받을 보상의 ID", example = "1")
            @PathVariable Long rewardId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("유저 상품 등록 API 호출: rewardId={}", rewardId);

        UserRewardsResponse rewardResponse = userRewardsService.redeemUserRewards(rewardId, principal.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 지급되었습니다"));
    }

    @Operation(
        summary = "받은 보상 사용하기", 
        description = "받은 보상을 사용 처리합니다. 사용된 보상은 다시 사용할 수 없습니다."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> useUserReward(
            @Parameter(description = "사용할 받은 보상의 ID", example = "1")
            @PathVariable Long id) {
        log.info("유저 상품 사용 API 호출: id={}", id);

        UserRewardsResponse rewardResponse = userRewardsService.useUserRewards(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 사용 완료되었습니다."));
    }
}
