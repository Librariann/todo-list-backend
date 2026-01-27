package com.librarian.todo_list.challenges.controller;

import com.librarian.todo_list.challenges.dto.ChallengesRegistrationRequest;
import com.librarian.todo_list.challenges.dto.ChallengesResponse;
import com.librarian.todo_list.challenges.dto.ChallengesUpdateRequest;
import com.librarian.todo_list.challenges.service.ChallengesService;
import com.librarian.todo_list.common.dto.ApiResponse;
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
 * 도전과제(Challenges) 관리 REST API 컨트롤러
 */
@Tag(name = "도전과제 관리", description = "도전과제 조회, 등록, 수정, 삭제 API")
@Slf4j
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ChallengesController {
    private final ChallengesService challengesService;

    @Operation(
        summary = "모든 도전과제 목록 조회", 
        description = "시스템에 등록된 모든 도전과제를 조회합니다. 일일/주간/월간 도전과제가 포함됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "도전과제 목록 조회 성공"
        )
    })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ChallengesResponse>>> getChallenges() {
        log.info("상품 목록 API 호출");

        List<ChallengesResponse> challengesResponse = challengesService.getChallenges();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(challengesResponse, "도전과제 목록을 성공적으로 불러왔습니다."));
    }

    @Operation(
        summary = "새로운 도전과제 등록", 
        description = "새로운 도전과제를 시스템에 등록합니다. TODOS, HABITS, GOALS 타입을 지원하며 일일/주간/월간 주기를 설정할 수 있습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "도전과제 등록 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 데이터"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ChallengesResponse>> registerChallenge(
            @Valid @RequestBody ChallengesRegistrationRequest request) {
        log.info("도전과제 등록 API 호출: name={}", request.getName());

        ChallengesResponse challengesResponse = challengesService.registerChallenges(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(challengesResponse, "도전과제가 성공적으로 등록 완료되었습니다."));
    }

    @Operation(
        summary = "도전과제 정보 수정", 
        description = "기존 도전과제의 정보를 수정합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "도전과제 수정 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "도전과제를 찾을 수 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 데이터"
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateChallenge(
            @Valid @RequestBody ChallengesUpdateRequest request,
            @Parameter(description = "수정할 도전과제의 ID", example = "1")
            @PathVariable Long id ) {
        log.info("상품 수정 API 호출: name={}", request.getName());

        ChallengesResponse challengesResponse = challengesService.updateChallenges(request, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(challengesResponse, "도전과제를 성공적으로 수정 했습니다."));
    }

    @Operation(
        summary = "도전과제 삭제", 
        description = "도전과제를 시스템에서 완전히 삭제합니다. 삭제된 데이터는 복구할 수 없습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "도전과제 삭제 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "도전과제를 찾을 수 없음"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ChallengesResponse>> deleteChallenge(
            @Parameter(description = "삭제할 도전과제의 ID", example = "1")
            @PathVariable Long id) {
        log.info("상품 삭제 API 호출: id={}", id);

        ChallengesResponse challengesResponse = challengesService.deleteChallenges(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(challengesResponse, "도전과제를 성공적으로 삭제 완료되었습니다."));
    }
}
