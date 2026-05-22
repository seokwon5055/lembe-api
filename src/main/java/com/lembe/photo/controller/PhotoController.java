package com.lembe.photo.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.photo.dto.PhotoResponse;
import com.lembe.photo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PhotoResponse> upload(
            @AuthenticationPrincipal Long userSeq,
            @RequestParam("file") MultipartFile file,
            @RequestParam("photoType") String photoType,
            @RequestParam(value = "milestoneSeq", required = false) Long milestoneSeq) {
        return ApiResponse.ok(photoService.upload(userSeq, file, photoType, milestoneSeq));
    }

    @GetMapping
    public ApiResponse<List<PhotoResponse>> getPhotos(
            @AuthenticationPrincipal Long userSeq,
            @RequestParam(required = false) String photoType) {
        return ApiResponse.ok(photoService.getPhotos(userSeq, photoType));
    }

    @PutMapping("/{photoSeq}/main")
    public ApiResponse<PhotoResponse> setMain(
            @AuthenticationPrincipal Long userSeq,
            @PathVariable Long photoSeq) {
        return ApiResponse.ok(photoService.setMain(userSeq, photoSeq));
    }

    @DeleteMapping("/{photoSeq}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal Long userSeq,
            @PathVariable Long photoSeq) {
        photoService.delete(userSeq, photoSeq);
        return ApiResponse.ok();
    }
}
