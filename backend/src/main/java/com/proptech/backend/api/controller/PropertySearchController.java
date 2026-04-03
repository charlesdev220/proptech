package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.PagePropertyDTO;
import com.proptech.backend.api.dto.PropertySearchRequest;
import com.proptech.backend.domain.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/properties/search")
@RequiredArgsConstructor
public class PropertySearchController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PagePropertyDTO> search(@RequestBody PropertySearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;

        var results = propertyService.searchProperties(request, PageRequest.of(page, size));

        PagePropertyDTO response = new PagePropertyDTO();
        response.setContent(results.getContent());
        response.setTotalElements((int) results.getTotalElements());
        response.setTotalPages(results.getTotalPages());
        response.setSize(results.getSize());
        response.setNumber(results.getNumber());

        return ResponseEntity.ok(response);
    }
}
