package com.lembe.photo.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.photo.domain.Photo;
import com.lembe.photo.dto.PhotoResponse;
import com.lembe.photo.mapper.PhotoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private static final List<String> VALID_TYPES =
            List.of("FACE", "BODY", "PROGRESS", "AI_GENERATED");

    private final PhotoMapper photoMapper;
    private final StorageService storageService;

    @Transactional
    public PhotoResponse upload(Long userSeq, MultipartFile file,
                                String photoType, Long milestoneSeq) {
        String normalizedType = photoType.toUpperCase();
        if (!VALID_TYPES.contains(normalizedType)) {
            throw new LembeException(ErrorCode.INVALID_INPUT,
                    "photo_type은 FACE, BODY, PROGRESS, AI_GENERATED 중 하나여야 합니다.");
        }

        StorageResult stored = storageService.store(file, userSeq, normalizedType);

        Photo photo = Photo.builder()
                .userSeq(userSeq)
                .milestoneSeq(milestoneSeq)
                .photoType(normalizedType)
                .storageKey(stored.storagePath())
                .cdnUrl(stored.accessUrl())
                .isMain(false)
                .version(1)
                .takenAt(LocalDateTime.now())
                .build();

        photoMapper.insert(photo);
        return PhotoResponse.from(photo);
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotos(Long userSeq, String photoType) {
        String type = (photoType != null) ? photoType.toUpperCase() : null;
        return photoMapper.findByUserSeq(userSeq, type)
                .stream().map(PhotoResponse::from).toList();
    }

    @Transactional
    public PhotoResponse setMain(Long userSeq, Long photoSeq) {
        Photo photo = photoMapper.findById(photoSeq);
        if (photo == null || !photo.getUserSeq().equals(userSeq)) {
            throw new LembeException(ErrorCode.PHOTO_NOT_FOUND);
        }
        photoMapper.updateMain(userSeq, photo.getPhotoType(), photoSeq);
        return PhotoResponse.from(photoMapper.findById(photoSeq));
    }

    @Transactional
    public void delete(Long userSeq, Long photoSeq) {
        Photo photo = photoMapper.findById(photoSeq);
        if (photo == null || !photo.getUserSeq().equals(userSeq)) {
            throw new LembeException(ErrorCode.PHOTO_NOT_FOUND);
        }
        photoMapper.softDelete(photoSeq, userSeq);
        storageService.delete(photo.getStorageKey());
    }
}
