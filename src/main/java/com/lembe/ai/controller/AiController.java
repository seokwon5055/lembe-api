package com.lembe.ai.controller;

import com.lembe.ai.dto.AiJobResponse;
import com.lembe.ai.dto.RegenerateRequest;
import com.lembe.ai.service.AiGenerationService;
import com.lembe.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiGenerationService aiGenerationService;

    /**
     * 초기 AI 사진 수동 트리거 (개발/테스트용 — 목표 생성 시 자동 호출됨)
     */
    @PostMapping("/generate/initial")
    public ApiResponse<Void> generateInitial(
            @AuthenticationPrincipal Long userSeq,
            @RequestParam Long goalSeq) {
        aiGenerationService.generateInitial(userSeq, goalSeq);
        return ApiResponse.ok(null);
    }

    /**
     * 마일스톤 AI 사진 재생성
     * isFreeSameImage=true: 무료 1회 (같은 스타일 재생성)
     * isFreeSameImage=false: 유료 (새 스타일 생성)
     */
    @PostMapping("/jobs/{milestoneSeq}/regenerate")
    public ApiResponse<AiJobResponse> regenerate(
            @AuthenticationPrincipal Long userSeq,
            @PathVariable Long milestoneSeq,
            @Valid @RequestBody RegenerateRequest request) {
        return ApiResponse.ok(
                aiGenerationService.regenerate(userSeq, milestoneSeq, request.isFreeSameImage()));
    }

    /**
     * AI 생성 작업 상태 조회
     */
    @GetMapping("/jobs/{genSeq}")
    public ApiResponse<AiJobResponse> getJob(
            @AuthenticationPrincipal Long userSeq,
            @PathVariable Long genSeq) {
        return ApiResponse.ok(aiGenerationService.getJob(genSeq));
    }
}
