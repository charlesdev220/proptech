package com.proptech.backend.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptech.backend.api.dto.SavedSearchCreateRequest;
import com.proptech.backend.infrastructure.persistence.entity.SavedSearchEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.SavedSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedSearchServiceTest {

    @Mock
    private SavedSearchRepository savedSearchRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SavedSearchService savedSearchService;

    @Test
    void create_whenLimitReached_throws422() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        
        when(savedSearchRepository.countByUserId(user.getId())).thenReturn(10L);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> savedSearchService.create(new SavedSearchCreateRequest(), user));
        
        assertEquals(422, exception.getStatusCode().value());
        assertEquals("Límite de 10 búsquedas guardadas alcanzado", exception.getReason());
        verify(savedSearchRepository, never()).save(any());
    }

    @Test
    void delete_whenOwnershipMismatch_throws403() {
        // Arrange
        UUID searchId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        UserEntity owner = new UserEntity();
        owner.setId(ownerId);

        SavedSearchEntity entity = new SavedSearchEntity();
        entity.setId(searchId);
        entity.setUser(owner);

        when(savedSearchRepository.findById(searchId)).thenReturn(Optional.of(entity));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> savedSearchService.delete(searchId, otherUserId));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(savedSearchRepository, never()).deleteById(any());
    }

    @Test
    void toggleActive_whenOwnershipMismatch_throws403() {
        // Arrange
        UUID searchId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        UserEntity owner = new UserEntity();
        owner.setId(ownerId);

        SavedSearchEntity entity = new SavedSearchEntity();
        entity.setId(searchId);
        entity.setUser(owner);

        when(savedSearchRepository.findById(searchId)).thenReturn(Optional.of(entity));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> savedSearchService.toggleActive(searchId, otherUserId, false));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(savedSearchRepository, never()).save(any());
    }
}
