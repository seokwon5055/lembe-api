package com.lembe.goal.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.goal.dto.MilestoneResponse;
import com.lembe.goal.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping("/{milestoneSeq}/unlock")
    public ApiResponse<MilestoneResponse> unlock(
            @AuthenticationPrincipal Long userSeq,
            @PathVariable Long milestoneSeq) {
        return ApiResponse.ok(milestoneService.unlock(userSeq, milestoneSeq));
    }
}
