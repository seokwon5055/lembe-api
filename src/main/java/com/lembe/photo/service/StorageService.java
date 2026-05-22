package com.lembe.photo.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StorageResult store(MultipartFile file, Long userId, String photoType);

    void delete(String storagePath);
}
