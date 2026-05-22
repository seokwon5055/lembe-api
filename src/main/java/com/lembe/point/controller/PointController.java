package com.lembe.point.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.point.dto.RouletteResponse;
import com.lembe.point.dto.TransactionHistoryResponse;
import com.lembe.point.dto.WalletResponse;
import com.lembe.point.dto.PointTransactionResponse;
import com.lembe.point.mapper.PointLogMapper;
import com.lembe.point.service.RouletteService;
import com.lembe.user.domain.PointWallet;
import com.lembe.user.mapper.PointWalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    private final PointWalletMapper pointWalletMapper;
    private final PointLogMapper pointLogMapper;
    private final RouletteService rouletteService;

    @GetMapping("/wallet")
    public ApiResponse<WalletResponse> wallet(@AuthenticationPrincipal Long userSeq) {
        PointWallet wallet = pointWalletMapper.findByUserSeq(userSeq);
        if (wallet == null) throw new LembeException(ErrorCode.NOT_FOUND, "지갑이 없습니다.");
        return ApiResponse.ok(WalletResponse.from(wallet));
    }

    @GetMapping("/transactions")
    public ApiResponse<TransactionHistoryResponse> transactions(
            @AuthenticationPrincipal Long userSeq,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        int offset = (page - 1) * size;
        List<PointTransactionResponse> items = pointLogMapper
                .findHistory(userSeq, offset, size)
                .stream().map(PointTransactionResponse::from).toList();
        int total = pointLogMapper.countByUserSeq(userSeq);

        return ApiResponse.ok(new TransactionHistoryResponse(items, total, page, size));
    }

    @PostMapping("/roulette")
    public ApiResponse<RouletteResponse> roulette(@AuthenticationPrincipal Long userSeq) {
        return ApiResponse.ok(rouletteService.spin(userSeq));
    }

    @PostMapping("/roulette/ad")
    public ApiResponse<RouletteResponse> rouletteAd(@AuthenticationPrincipal Long userSeq) {
        return ApiResponse.ok(rouletteService.spinAd(userSeq));
    }
}
