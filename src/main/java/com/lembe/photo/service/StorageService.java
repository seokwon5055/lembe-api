package com.lembe.photo.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StorageResult store(MultipartFile file, String ucode, String photoType);

    void delete(String storagePath);
}
