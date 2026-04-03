package com.proptech.backend.domain.service;

import com.proptech.backend.domain.exception.MediaNotFoundException;
import com.proptech.backend.infrastructure.persistence.entity.MediaEntity;
import com.proptech.backend.infrastructure.persistence.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    @Transactional
    public String saveMedia(MultipartFile file) throws IOException {
        MediaEntity media = new MediaEntity();
        media.setFileName(file.getOriginalFilename());
        media.setContentType(file.getContentType());
        media.setData(file.getBytes());
        media.setSize(file.getSize());

        MediaEntity saved = mediaRepository.save(media);
        return saved.getId().toString();
    }

    @Transactional(readOnly = true)
    public MediaEntity getMedia(UUID id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException(id.toString()));
    }
}
