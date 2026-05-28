package com.lembe.ai.dto;

import jakarta.validation.constraints.NotNull;

public record RegenerateRequest(
        @NotNull Boolean isFreeSameImage
) {}
