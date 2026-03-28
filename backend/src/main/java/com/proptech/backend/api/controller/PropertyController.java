package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.PropertyCreateDTO;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.domain.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<Page<PropertyDTO>> searchProperties(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius,
            @PageableDefault(size = 20) Pageable pageable) {
        
        return ResponseEntity.ok(propertyService.searchProperties(minPrice, maxPrice, lat, lng, radius, pageable));
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyCreateDTO propertyDto) {
        return ResponseEntity.status(201).body(propertyService.createProperty(propertyDto));
    }
}
