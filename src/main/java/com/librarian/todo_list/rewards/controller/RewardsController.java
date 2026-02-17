package com.librarian.todo_list.rewards.controller;

import com.librarian.todo_list.common.dto.ApiResponse;
import com.librarian.todo_list.rewards.dto.RewardsRegistrationRequest;
import com.librarian.todo_list.rewards.dto.RewardsResponse;
import com.librarian.todo_list.rewards.dto.RewardsUpdateRequest;
import com.librarian.todo_list.rewards.service.RewardsService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 보상(Rewards) 관리 REST API 컨트롤러
 */
@Tag(name = "보상 관리", description = "보상 조회, 등록, 수정, 삭제 API")
@Slf4j
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RewardsController {
    private final RewardsService rewardsService;

    @Operation(
        summary = "모든 보상 목록 조회", 
        description = "시스템에 등록된 모든 보상 항목을 조회합니다."
    )
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<RewardsResponse>>> getRewardList() {
        log.info("상품 목록 API 호출");

        List<RewardsResponse> rewardResponse = rewardsService.getRewards();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상 목록을 성공적으로 불러왔습니다."));
    }

    @Operation(
        summary = "특정 보상 조회", 
        description = "보상 ID를 통해 특정 보상의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardsResponse>> getOneReward(
            @Parameter(description = "조회할 보상의 ID", example = "1")
            @PathVariable Long id) {
        log.info("특정 상품 API 호출: id={}", id);

        RewardsResponse rewardResponse = rewardsService.getOneReward(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상을 성공적으로 불러왔습니다."));
    }

    @Operation(
        summary = "새로운 보상 등록", 
        description = "새로운 보상 항목을 시스템에 등록합니다. 관리자 권한이 필요할 수 있습니다."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RewardsResponse>> registerReward(
            @Valid @RequestBody RewardsRegistrationRequest request) {
        log.info("상품 등록 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.registerRewards(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 등록 완료되었습니다."));
    }

    @Operation(
        summary = "보상 정보 수정", 
        description = "기존 보상의 정보를 수정합니다."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateReward(
            @Valid @RequestBody RewardsUpdateRequest request,
            @Parameter(description = "수정할 보상의 ID", example = "1")
            @PathVariable Long id ) {
        log.info("상품 수정 API 호출: name={}", request.getName());

        RewardsResponse rewardResponse = rewardsService.updateRewards(request, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 수정 완료되었습니다."));
    }

    @Operation(
        summary = "보상 삭제", 
        description = "보상을 시스템에서 완전히 삭제합니다. 삭제된 데이터는 복구할 수 없습니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardsResponse>> deleteReward(
            @Parameter(description = "삭제할 보상의 ID", example = "1")
            @PathVariable Long id) {
        log.info("상품 삭제 API 호출: id={}", id);

        RewardsResponse rewardResponse = rewardsService.deleteRewards(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(rewardResponse, "보상이 성공적으로 삭제 완료되었습니다."));
    }
}
