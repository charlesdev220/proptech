package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.domain.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addFavorite(@PathVariable UUID propertyId) {
        favoriteService.addFavorite(propertyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID propertyId) {
        favoriteService.removeFavorite(propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PropertyDTO>> getUserFavorites() {
        return ResponseEntity.ok(favoriteService.getUserFavorites());
    }

    @GetMapping("/ids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, List<UUID>>> getUserFavoriteIds() {
        List<UUID> ids = favoriteService.getUserFavoriteIds();
        return ResponseEntity.ok(Map.of("ids", ids));
    }
}
