package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.NeighborhoodDTO;
import com.proptech.backend.infrastructure.mapper.NeighborhoodMapper;
import com.proptech.backend.infrastructure.persistence.repository.NeighborhoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/neighborhoods")
@RequiredArgsConstructor
public class NeighborhoodController {

    private final NeighborhoodRepository neighborhoodRepository;
    private final NeighborhoodMapper neighborhoodMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<NeighborhoodDTO>> getAll() {
        List<NeighborhoodDTO> dtos = neighborhoodRepository.findAllByOrderByNameAsc()
            .stream()
            .map(neighborhoodMapper::toDto)
            .toList();
        return ResponseEntity.ok(dtos);
    }
}
