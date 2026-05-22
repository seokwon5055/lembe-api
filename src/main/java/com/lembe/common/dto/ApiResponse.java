package com.lembe.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lembe.common.exception.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String result,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("00", "OK", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>("00", "OK", null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
