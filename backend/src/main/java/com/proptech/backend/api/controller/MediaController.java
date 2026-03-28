package com.proptech.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.UUID;

import com.proptech.backend.domain.service.AwsS3Service;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final AwsS3Service awsS3Service;

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@RequestParam String extension, @RequestParam String contentType) {
        String presignedUrl = awsS3Service.generatePresignedUrl(extension, contentType);
        return ResponseEntity.ok(Map.of("uploadUrl", presignedUrl));
    }
}
