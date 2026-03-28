package com.proptech.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

import com.proptech.backend.domain.service.PropertyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProperty(@RequestBody Map<String, Object> propertyDto) {
        return ResponseEntity.status(201).body(propertyService.createProperty(propertyDto));
    }
}
