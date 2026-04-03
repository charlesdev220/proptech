package com.proptech.backend.api.controller;

import com.proptech.backend.api.PropertiesApi;
import com.proptech.backend.api.dto.MediaDTO;
import com.proptech.backend.api.dto.PagePropertyDTO;
import com.proptech.backend.api.dto.PropertyCreateDTO;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.api.dto.PropertyDetailDTO;
import com.proptech.backend.domain.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PropertyController implements PropertiesApi {

    private final PropertyService propertyService;

    @Override
    public ResponseEntity<PagePropertyDTO> propertiesGet(
            Integer page, Integer size, BigDecimal minPrice, BigDecimal maxPrice,
            Float lat, Float lng, Float radius) {
        
        var results = propertyService.searchProperties(
                minPrice, maxPrice, 
                lat != null ? lat.doubleValue() : null, 
                lng != null ? lng.doubleValue() : null, 
                radius != null ? radius.doubleValue() : null, 
                PageRequest.of(page, size)
        );

        PagePropertyDTO response = new PagePropertyDTO();
        response.setContent(results.getContent());
        response.setTotalElements((int) results.getTotalElements());
        response.setTotalPages(results.getTotalPages());
        response.setSize(results.getSize());
        response.setNumber(results.getNumber());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PropertyDetailDTO> propertiesIdGet(UUID id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @Override
    public ResponseEntity<List<MediaDTO>> propertiesIdMediaGet(UUID id) {
        return ResponseEntity.ok(propertyService.getMediaForProperty(id));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PropertyDTO> propertiesPost(PropertyCreateDTO propertyCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propertyService.createProperty(propertyCreateDTO));
    }
}
