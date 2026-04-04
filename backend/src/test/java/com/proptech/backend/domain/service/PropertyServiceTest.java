package com.proptech.backend.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.api.dto.PropertySearchRequest;
import com.proptech.backend.api.dto.GeoJsonGeometry;
import com.proptech.backend.infrastructure.mapper.PropertyMapper;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.repository.MediaRepository;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private PropertyMapper propertyMapper;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void searchProperties_withPolygon_callsSearchWithPolygon() throws JsonProcessingException {
        // Arrange
        PropertySearchRequest request = new PropertySearchRequest();
        GeoJsonGeometry polygon = new GeoJsonGeometry();
        polygon.setType(GeoJsonGeometry.TypeEnum.POLYGON);
        request.setPolygon(polygon);
        request.setMinPrice(new BigDecimal("100000"));
        request.setMaxPrice(new BigDecimal("500000"));
        request.setMinRooms(2);

        Pageable pageable = PageRequest.of(0, 20);
        String geoJsonStr = "{\"type\":\"Polygon\"}";

        when(objectMapper.writeValueAsString(any())).thenReturn(geoJsonStr);
        
        PropertyEntity entity = new PropertyEntity();
        entity.setId(UUID.randomUUID());
        Page<PropertyEntity> mockPage = new PageImpl<>(List.of(entity));
        
        when(propertyRepository.searchWithPolygon(eq(geoJsonStr), eq(request.getMinPrice()), 
                eq(request.getMaxPrice()), eq(request.getMinRooms()), eq(pageable)))
                .thenReturn(mockPage);
        
        when(propertyMapper.toDto(any())).thenReturn(new PropertyDTO());
        when(mediaRepository.findFirstMediaByPropertyIds(anyList())).thenReturn(Collections.emptyList());

        // Act
        Page<PropertyDTO> result = propertyService.searchProperties(request, pageable);

        // Assert
        assertNotNull(result);
        verify(propertyRepository).searchWithPolygon(eq(geoJsonStr), any(), any(), any(), any());
        verify(propertyRepository, never()).searchProperties(any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchProperties_withoutPolygon_callsStandardSearch() {
        // Arrange
        PropertySearchRequest request = new PropertySearchRequest();
        request.setLat(40.4168f);
        request.setLng(-3.7038f);
        request.setRadius(1000f);

        Pageable pageable = PageRequest.of(0, 20);
        
        Page<PropertyEntity> mockPage = new PageImpl<>(Collections.emptyList());
        when(propertyRepository.searchProperties(any(), any(), any(), any(), any(), any()))
                .thenReturn(mockPage);

        // Act
        propertyService.searchProperties(request, pageable);

        // Assert
        verify(propertyRepository).searchProperties(any(), any(), anyDouble(), anyDouble(), anyDouble(), any());
        verify(propertyRepository, never()).searchWithPolygon(anyString(), any(), any(), any(), any());
    }
}
