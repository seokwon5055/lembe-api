package com.lembe.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT("01", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("02", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("03", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("04", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("99", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Auth
    INVALID_TOKEN("10", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("11", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    DUPLICATE_EMAIL("12", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),

    // User
    USER_NOT_FOUND("20", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Goal
    GOAL_NOT_FOUND("30", "목표를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Milestone
    MILESTONE_NOT_FOUND("40", "마일스톤을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MILESTONE_NOT_READY("41", "아직 열 수 없는 마일스톤입니다. 목표 체중에 도달하세요.", HttpStatus.BAD_REQUEST),
    MILESTONE_ALREADY_UNLOCKED("42", "이미 열린 마일스톤입니다.", HttpStatus.BAD_REQUEST),

    // Photo
    PHOTO_NOT_FOUND("50", "사진을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Point
    INSUFFICIENT_POINTS("60", "포인트가 부족합니다.", HttpStatus.BAD_REQUEST),

    // AI
    AI_GENERATION_FAILED("70", "AI 이미지 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_JOB_NOT_FOUND("71", "AI 생성 작업을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AI_DAILY_LIMIT_EXCEEDED("72", "일일 AI 생성 한도(10회)를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS),
    AI_GLOBAL_LIMIT_EXCEEDED("73", "서비스 AI 한도 초과. 잠시 후 다시 시도해주세요.", HttpStatus.SERVICE_UNAVAILABLE),
    AI_FREE_RETRY_EXHAUSTED("74", "무료 재생성 기회를 이미 사용했습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
