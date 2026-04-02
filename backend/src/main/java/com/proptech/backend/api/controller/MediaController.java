package com.proptech.backend.api.controller;

import com.proptech.backend.domain.service.MediaService;
import com.proptech.backend.infrastructure.persistence.entity.MediaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            String id = mediaService.saveMedia(file);
            
            // Build absolute URL for the frontend / database
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/media/")
                    .path(id)
                    .toUriString();
                    
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", id,
                    "url", fileDownloadUri,
                    "fileName", file.getOriginalFilename(),
                    "size", file.getSize()
                ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getMedia(@PathVariable UUID id) {
        MediaEntity media = mediaService.getMedia(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.getFileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000") // 1 year cache
                .contentType(MediaType.parseMediaType(media.getContentType()))
                .body(media.getData());
    }
}
