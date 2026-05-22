package com.lembe.photo.service;

import com.lembe.common.config.StorageProperties;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".heic", ".webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10 MB

    private final StorageProperties storageProperties;

    @Override
    public StorageResult store(MultipartFile file, Long userId, String photoType) {
        validateFile(file);

        String ext = resolveExtension(file.getOriginalFilename());
        String relativePath = userId + "/" + photoType.toLowerCase() + "/" + UUID.randomUUID() + ext;

        Path fullPath = Paths.get(storageProperties.getLocal().getBasePath()).resolve(relativePath);
        try {
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("[Storage] file write failed: {}", relativePath, e);
            throw new LembeException(ErrorCode.INTERNAL_ERROR, "파일 저장에 실패했습니다.");
        }

        String url = storageProperties.getLocal().getBaseUrl() + "/" + relativePath;
        return new StorageResult(relativePath, url);
    }

    @Override
    public void delete(String storagePath) {
        if (!StringUtils.hasText(storagePath)) return;
        try {
            Path fullPath = Paths.get(storageProperties.getLocal().getBasePath()).resolve(storagePath);
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.warn("[Storage] file delete failed: {}", storagePath, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new LembeException(ErrorCode.INVALID_INPUT, "파일이 비어있습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new LembeException(ErrorCode.INVALID_INPUT, "파일 크기는 10MB 이하여야 합니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new LembeException(ErrorCode.INVALID_INPUT, "이미지 파일만 업로드 가능합니다.");
        }
    }

    private String resolveExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
            if (ALLOWED_EXTENSIONS.contains(ext)) return ext;
        }
        return ".jpg";
    }
}
