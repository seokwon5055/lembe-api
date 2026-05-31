package com.lembe.goal.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.goal.dto.CreateGoalRequest;
import com.lembe.goal.dto.GoalWithMilestonesResponse;
import com.lembe.goal.dto.MilestoneResponse;
import com.lembe.goal.dto.UpdateGoalRequest;
import com.lembe.goal.service.GoalService;
import com.lembe.goal.service.MilestoneService;
import com.lembe.photo.dto.PhotoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final MilestoneService milestoneService;

    @PostMapping("/goals")
    public ApiResponse<GoalWithMilestonesResponse> createGoal(
            @AuthenticationPrincipal String ucode,
            @Valid @RequestBody CreateGoalRequest request) {
        return ApiResponse.ok(goalService.createGoal(ucode, request));
    }

    @GetMapping("/goals/current")
    public ApiResponse<GoalWithMilestonesResponse> getCurrentGoal(
            @AuthenticationPrincipal String ucode) {
        return ApiResponse.ok(goalService.getCurrentGoal(ucode));
    }

    @PutMapping("/goals/{goalSeq}")
    public ApiResponse<GoalWithMilestonesResponse> updateGoal(
            @AuthenticationPrincipal String ucode,
            @PathVariable Long goalSeq,
            @Valid @RequestBody UpdateGoalRequest request) {
        return ApiResponse.ok(goalService.updateGoal(ucode, goalSeq, request));
    }

    @GetMapping("/milestones")
    public ApiResponse<List<MilestoneResponse>> getMilestones(
            @AuthenticationPrincipal String ucode) {
        return ApiResponse.ok(goalService.getMilestones(ucode));
    }

    @PostMapping("/milestones/{milestoneSeq}/unlock")
    public ApiResponse<MilestoneResponse> unlockMilestone(
            @AuthenticationPrincipal String ucode,
            @PathVariable Long milestoneSeq) {
        return ApiResponse.ok(milestoneService.unlock(ucode, milestoneSeq));
    }

    @PostMapping("/milestones/{milestoneSeq}/upload-progress")
    public ApiResponse<PhotoResponse> uploadProgressPhoto(
            @AuthenticationPrincipal String ucode,
            @PathVariable Long milestoneSeq,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(milestoneService.uploadProgressPhoto(ucode, milestoneSeq, file));
    }
}
