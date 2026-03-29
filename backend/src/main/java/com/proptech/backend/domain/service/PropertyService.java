package com.proptech.backend.domain.service;

import com.proptech.backend.api.dto.PropertyCreateDTO;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.infrastructure.mapper.PropertyMapper;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import com.proptech.backend.api.dto.PropertyDetailDTO;
import com.proptech.backend.api.dto.MediaDTO;
import com.proptech.backend.infrastructure.persistence.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final PropertyMapper propertyMapper;

    @Transactional(readOnly = true)
    public Page<PropertyDTO> searchProperties(
            BigDecimal minPrice, BigDecimal maxPrice,
            Double lat, Double lng, Double radius,
            Pageable pageable) {
        
        return propertyRepository.searchProperties(minPrice, maxPrice, lat, lng, radius, pageable)
                .map(propertyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PropertyDetailDTO getPropertyById(UUID id) {
        return propertyRepository.findById(id)
                .map(propertyMapper::toDetailDto)
                .orElseThrow(() -> new RuntimeException("Inmueble no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<MediaDTO> getMediaForProperty(UUID id) {
        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inmueble no encontrado: " + id));
        
        return property.getMediaFiles().stream()
                .map(m -> {
                    MediaDTO dto = new MediaDTO();
                    dto.setFileName(m.getFileName());
                    dto.setSize(m.getSize());
                    // Note: URL generation logic should probably be centralized
                    dto.setUrl(java.net.URI.create("/api/v1/media/" + m.getId())); 
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public PropertyDTO createProperty(PropertyCreateDTO dto) {
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        var owner = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en sesión: " + currentUserEmail));

        PropertyEntity entity = propertyMapper.toEntity(dto);
        entity.setOwner(owner);

        // Link media if provided
        if (dto.getMediaIds() != null && !dto.getMediaIds().isEmpty()) {
            var mediaFiles = mediaRepository.findAllById(dto.getMediaIds());
            mediaFiles.forEach(m -> m.setProperty(entity));
            entity.setMediaFiles(mediaFiles);
        }
        
        PropertyEntity saved = propertyRepository.save(entity);
        return propertyMapper.toDto(saved);
    }
}
