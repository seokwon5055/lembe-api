package com.lembe.purchase.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.purchase.dto.PurchaseHistoryResponse;
import com.lembe.purchase.dto.PurchaseRequest;
import com.lembe.purchase.dto.PurchaseResponse;
import com.lembe.purchase.dto.RefundWebhookRequest;
import com.lembe.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/points/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ApiResponse<PurchaseResponse> purchase(
            @AuthenticationPrincipal String ucode,
            @Valid @RequestBody PurchaseRequest request) {
        return ApiResponse.ok(purchaseService.purchase(ucode, request));
    }

    @GetMapping("/history")
    public ApiResponse<PurchaseHistoryResponse> history(
            @AuthenticationPrincipal String ucode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(purchaseService.getHistory(ucode, page, size));
    }

    /**
     * 환불 웹훅 — 애플/구글 서버에서 호출
     * TODO: 실제 연동 전 웹훅 서명 검증 로직 추가 (HMAC or 공개키 검증)
     */
    @PostMapping("/refund")
    public ApiResponse<Void> refund(@Valid @RequestBody RefundWebhookRequest request) {
        purchaseService.processRefund(request);
        return ApiResponse.ok(null);
    }
}
