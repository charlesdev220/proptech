package com.proptech.backend.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptech.backend.api.dto.PropertySearchRequest;
import com.proptech.backend.api.dto.SavedSearchCreateRequest;
import com.proptech.backend.api.dto.SavedSearchDTO;
import com.proptech.backend.infrastructure.persistence.entity.SavedSearchEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.SavedSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedSearchService {

    private static final int MAX_SAVED_SEARCHES_PER_USER = 10;

    private final SavedSearchRepository savedSearchRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SavedSearchDTO create(SavedSearchCreateRequest req, UserEntity user) {
        if (savedSearchRepository.countByUserId(user.getId()) >= MAX_SAVED_SEARCHES_PER_USER) {
            throw new ResponseStatusException(
                HttpStatusCode.valueOf(422),
                "Límite de 10 búsquedas guardadas alcanzado"
            );
        }

        String filtersJson;
        try {
            filtersJson = objectMapper.writeValueAsString(req.getFilters());
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtros de búsqueda inválidos");
        }

        SavedSearchEntity entity = SavedSearchEntity.builder()
            .user(user)
            .name(req.getName())
            .filters(filtersJson)
            .active(true)
            .build();

        return toDto(savedSearchRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<SavedSearchDTO> findAllForUser(UUID userId) {
        return savedSearchRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional
    public SavedSearchDTO toggleActive(UUID id, UUID userId, boolean active) {
        SavedSearchEntity entity = savedSearchRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Búsqueda guardada no encontrada"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para modificar esta búsqueda");
        }

        entity.setActive(active);
        return toDto(savedSearchRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        SavedSearchEntity entity = savedSearchRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Búsqueda guardada no encontrada"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar esta búsqueda");
        }

        savedSearchRepository.deleteById(id);
    }

    private SavedSearchDTO toDto(SavedSearchEntity entity) {
        SavedSearchDTO dto = new SavedSearchDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setActive(entity.isActive());

        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        }
        if (entity.getLastCheckedAt() != null) {
            dto.setLastCheckedAt(entity.getLastCheckedAt().atOffset(ZoneOffset.UTC));
        }

        try {
            PropertySearchRequest filters = objectMapper.readValue(entity.getFilters(), PropertySearchRequest.class);
            dto.setFilters(filters);
        } catch (JsonProcessingException e) {
            // Si el JSON guardado es inválido, dejamos filters null — no propagamos error al cliente
            dto.setFilters(null);
        }

        return dto;
    }
}
