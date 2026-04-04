package com.proptech.backend.infrastructure.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptech.backend.api.dto.PropertySearchRequest;
import com.proptech.backend.domain.service.EmailNotificationService;
import com.proptech.backend.infrastructure.mapper.PropertyMapper;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.SavedSearchEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.SavedSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedSearchJobTest {

    @Mock
    private SavedSearchRepository savedSearchRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private PropertyMapper propertyMapper;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SavedSearchJob savedSearchJob;

    @Test
    void evaluateSavedSearches_onlyProcessesActiveSearches() throws JsonProcessingException {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");

        SavedSearchEntity activeSearch = new SavedSearchEntity();
        activeSearch.setName("Search 1");
        activeSearch.setUser(user);
        activeSearch.setFilters("{\"minPrice\": 100}");
        activeSearch.setActive(true);

        when(savedSearchRepository.findAllByActiveTrueOrderByCreatedAtDesc())
                .thenReturn(List.of(activeSearch));
        
        when(objectMapper.readValue(anyString(), eq(PropertySearchRequest.class)))
                .thenReturn(new PropertySearchRequest());

        // Simulate match
        when(propertyRepository.findCreatedAfterWithFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(new PropertyEntity()));

        // Act
        savedSearchJob.evaluateSavedSearches();

        // Assert
        verify(emailNotificationService).sendSavedSearchAlert(eq("test@example.com"), eq("Search 1"), anyList());
        verify(savedSearchRepository).save(activeSearch);
    }

    @Test
    void evaluateSavedSearches_doesNotSendEmailWhenNoMatches() throws JsonProcessingException {
        // Arrange
        SavedSearchEntity activeSearch = new SavedSearchEntity();
        activeSearch.setFilters("{}");
        activeSearch.setUser(new UserEntity());

        when(savedSearchRepository.findAllByActiveTrueOrderByCreatedAtDesc())
                .thenReturn(List.of(activeSearch));
        
        when(objectMapper.readValue(anyString(), eq(PropertySearchRequest.class)))
                .thenReturn(new PropertySearchRequest());

        when(propertyRepository.findCreatedAfterWithFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        savedSearchJob.evaluateSavedSearches();

        // Assert
        verify(emailNotificationService, never()).sendSavedSearchAlert(any(), any(), any());
    }
}
