package com.lembe.point.dto;

import java.util.List;

public record TransactionHistoryResponse(
        List<PointTransactionResponse> items,
        int total,
        int page,
        int size
) {}
