package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.MediaDTO;
import com.proptech.backend.api.dto.TrustScoreDTO;
import com.proptech.backend.api.dto.UserProfileDTO;
import com.proptech.backend.domain.service.MediaService;
import com.proptech.backend.domain.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private static final long MAX_DOCUMENT_SIZE = 5 * 1024 * 1024L; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");

    private final ProfileService profileService;
    private final MediaService mediaService;

    @GetMapping
    public ResponseEntity<UserProfileDTO> getCurrentProfile() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    @GetMapping("/trust-score")
    public ResponseEntity<TrustScoreDTO> getTrustScore() {
        return ResponseEntity.ok(profileService.getTrustScoreDetails());
    }

    @PostMapping("/documents")
    public ResponseEntity<MediaDTO> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                "Tipo de archivo no permitido. Se aceptan: image/jpeg, image/png, application/pdf"
            );
        }
        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new IllegalArgumentException("El archivo supera el límite de 5MB");
        }

        String mediaId = mediaService.saveMedia(file);

        URI fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/media/")
                .path(mediaId)
                .build()
                .toUri();

        MediaDTO dto = new MediaDTO();
        dto.setUrl(fileUri);
        dto.setFileName(file.getOriginalFilename());
        dto.setSize(file.getSize());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
