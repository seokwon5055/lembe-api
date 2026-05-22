package com.lembe.goal.dto;

import java.util.List;

public record GoalWithMilestonesResponse(
        GoalResponse goal,
        List<MilestoneResponse> milestones
) {}
