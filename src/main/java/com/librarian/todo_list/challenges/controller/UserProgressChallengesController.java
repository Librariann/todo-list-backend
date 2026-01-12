package com.librarian.todo_list.challenges.controller;

import com.librarian.todo_list.challenges.dto.ChallengesRegistrationRequest;
import com.librarian.todo_list.challenges.dto.ChallengesResponse;
import com.librarian.todo_list.challenges.dto.ChallengesUpdateRequest;
import com.librarian.todo_list.challenges.service.ChallengesService;
import com.librarian.todo_list.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengesController {
    private final ChallengesService challengesService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ChallengesResponse>>> getChallenges() {
        log.info("상품 목록 API 호출");

        List<ChallengesResponse> challengesResponse = challengesService.getChallenges();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(challengesResponse, "도전과제 목록을 성공적으로 불러왔습니다."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ChallengesResponse>> registerChallenge(
            @Valid @RequestBody ChallengesRegistrationRequest request) {
        log.info("도전과제 등록 API 호출: name={}", request.getName());

        ChallengesResponse challengesResponse = challengesService.registerChallenges(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(challengesResponse, "도전과제가 성공적으로 등록 완료되었습니다."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateChallenge(
            @Valid @RequestBody ChallengesUpdateRequest request,
            @PathVariable Long id ) {
        log.info("상품 수정 API 호출: name={}", request.getName());

        ChallengesResponse challengesResponse = challengesService.updateChallenges(request, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(challengesResponse, "도전과제를 성공적으로 수정 했습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ChallengesResponse>> deleteChallenge(@PathVariable Long id) {
        log.info("상품 삭제 API 호출: id={}", id);

        ChallengesResponse challengesResponse = challengesService.deleteChallenges(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(challengesResponse, "도전과제를 성공적으로 삭제 완료되었습니다."));
    }
}
