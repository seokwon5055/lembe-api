package com.lembe.goal.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.goal.dto.CreateGoalRequest;
import com.lembe.goal.dto.GoalWithMilestonesResponse;
import com.lembe.goal.dto.MilestoneResponse;
import com.lembe.goal.dto.UpdateGoalRequest;
import com.lembe.goal.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/goals")
    public ApiResponse<GoalWithMilestonesResponse> createGoal(
            @AuthenticationPrincipal Long userSeq,
            @Valid @RequestBody CreateGoalRequest request) {
        return ApiResponse.ok(goalService.createGoal(userSeq, request));
    }

    @GetMapping("/goals/current")
    public ApiResponse<GoalWithMilestonesResponse> getCurrentGoal(
            @AuthenticationPrincipal Long userSeq) {
        return ApiResponse.ok(goalService.getCurrentGoal(userSeq));
    }

    @PutMapping("/goals/{goalSeq}")
    public ApiResponse<GoalWithMilestonesResponse> updateGoal(
            @AuthenticationPrincipal Long userSeq,
            @PathVariable Long goalSeq,
            @Valid @RequestBody UpdateGoalRequest request) {
        return ApiResponse.ok(goalService.updateGoal(userSeq, goalSeq, request));
    }

    @GetMapping("/milestones")
    public ApiResponse<List<MilestoneResponse>> getMilestones(
            @AuthenticationPrincipal Long userSeq) {
        return ApiResponse.ok(goalService.getMilestones(userSeq));
    }
}
