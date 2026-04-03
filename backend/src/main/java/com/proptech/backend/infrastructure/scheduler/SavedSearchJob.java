package com.proptech.backend.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.api.dto.PropertySearchRequest;
import com.proptech.backend.domain.service.EmailNotificationService;
import com.proptech.backend.infrastructure.mapper.PropertyMapper;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.SavedSearchEntity;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.SavedSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SavedSearchJob {

    private final SavedSearchRepository savedSearchRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final EmailNotificationService emailNotificationService;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 * * * *")
    public void evaluateSavedSearches() {
        List<SavedSearchEntity> activeSearches = savedSearchRepository.findAllByActiveTrueOrderByCreatedAtDesc();
        log.info("SavedSearchJob: evaluando {} búsquedas activas.", activeSearches.size());

        for (SavedSearchEntity search : activeSearches) {
            try {
                PropertySearchRequest filters = objectMapper.readValue(search.getFilters(), PropertySearchRequest.class);

                LocalDateTime since = search.getLastCheckedAt() != null
                    ? search.getLastCheckedAt()
                    : LocalDateTime.now().minusHours(1);

                BigDecimal minPrice = filters.getMinPrice();
                BigDecimal maxPrice = filters.getMaxPrice();
                Integer minRooms = filters.getMinRooms();
                Double lat = filters.getLat() != null ? filters.getLat().doubleValue() : null;
                Double lng = filters.getLng() != null ? filters.getLng().doubleValue() : null;
                Double radius = filters.getRadius() != null ? filters.getRadius().doubleValue() : null;

                List<PropertyEntity> matches = propertyRepository.findCreatedAfterWithFilters(
                    since, minPrice, maxPrice, minRooms, lat, lng, radius
                );

                if (!matches.isEmpty()) {
                    List<PropertyDTO> dtos = matches.stream()
                        .map(propertyMapper::toDto)
                        .toList();
                    emailNotificationService.sendSavedSearchAlert(
                        search.getUser().getEmail(),
                        search.getName(),
                        dtos
                    );
                }

                search.setLastCheckedAt(LocalDateTime.now());
                savedSearchRepository.save(search);

            } catch (Exception e) {
                log.warn("Error evaluando búsqueda guardada {}: {}", search.getId(), e.getMessage());
            }
        }
    }
}
