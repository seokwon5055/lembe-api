package com.lembe.common.exception;

import lombok.Getter;

@Getter
public class LembeException extends RuntimeException {

    private final ErrorCode errorCode;

    public LembeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public LembeException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }
}
