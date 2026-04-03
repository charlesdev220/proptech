package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.SavedSearchCreateRequest;
import com.proptech.backend.api.dto.SavedSearchDTO;
import com.proptech.backend.api.dto.SavedSearchesIdPatchRequest;
import com.proptech.backend.domain.service.SavedSearchService;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/saved-searches")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class SavedSearchController {

    private final SavedSearchService savedSearchService;

    @GetMapping
    public ResponseEntity<List<SavedSearchDTO>> getAll(
            @AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(savedSearchService.findAllForUser(currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<SavedSearchDTO> create(
            @RequestBody SavedSearchCreateRequest request,
            @AuthenticationPrincipal UserEntity currentUser) {
        SavedSearchDTO created = savedSearchService.create(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SavedSearchDTO> toggleActive(
            @PathVariable UUID id,
            @RequestBody SavedSearchesIdPatchRequest request,
            @AuthenticationPrincipal UserEntity currentUser) {
        SavedSearchDTO updated = savedSearchService.toggleActive(id, currentUser.getId(), request.getActive());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity currentUser) {
        savedSearchService.delete(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
