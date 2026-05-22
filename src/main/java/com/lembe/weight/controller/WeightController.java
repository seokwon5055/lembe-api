package com.lembe.weight.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.weight.dto.WeightHistoryResponse;
import com.lembe.weight.dto.WeightRecordRequest;
import com.lembe.weight.dto.WeightRecordResponse;
import com.lembe.weight.service.WeightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weights")
@RequiredArgsConstructor
public class WeightController {

    private final WeightService weightService;

    @PostMapping
    public ApiResponse<WeightRecordResponse> record(
            @AuthenticationPrincipal Long userSeq,
            @Valid @RequestBody WeightRecordRequest request) {
        return ApiResponse.ok(weightService.record(userSeq, request));
    }

    @GetMapping("/history")
    public ApiResponse<WeightHistoryResponse> history(
            @AuthenticationPrincipal Long userSeq,
            @RequestParam(defaultValue = "30") int days) {
        return ApiResponse.ok(weightService.getHistory(userSeq, days));
    }

    @PostMapping("/sync")
    public ApiResponse<Void> sync(
            @AuthenticationPrincipal Long userSeq,
            @RequestBody List<@Valid WeightRecordRequest> records) {
        weightService.syncHealthData(userSeq, records);
        return ApiResponse.ok();
    }
}
