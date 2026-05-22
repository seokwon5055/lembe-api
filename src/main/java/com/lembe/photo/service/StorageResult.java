package com.lembe.photo.service;

public record StorageResult(
        String storagePath,   // DB에 저장되는 상대 경로 (삭제 시 사용)
        String accessUrl      // 클라이언트에 반환되는 접근 URL
) {}
