package com.lembe.purchase.dto;

public record VerificationResult(
        boolean valid,
        String errorMessage
) {
    public static VerificationResult ok() {
        return new VerificationResult(true, null);
    }

    public static VerificationResult fail(String reason) {
        return new VerificationResult(false, reason);
    }
}
