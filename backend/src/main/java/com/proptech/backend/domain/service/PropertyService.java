package com.proptech.backend.domain.service;

import com.proptech.backend.api.dto.PropertyCreateDTO;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.infrastructure.mapper.PropertyMapper;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyMapper propertyMapper;

    @Transactional(readOnly = true)
    public Page<PropertyDTO> searchProperties(
            BigDecimal minPrice, BigDecimal maxPrice,
            Double lat, Double lng, Double radius,
            Pageable pageable) {
        
        return propertyRepository.searchProperties(minPrice, maxPrice, lat, lng, radius, pageable)
                .map(propertyMapper::toDto);
    }

    @Transactional
    public PropertyDTO createProperty(PropertyCreateDTO dto) {
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        var owner = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en sesión: " + currentUserEmail));

        PropertyEntity entity = propertyMapper.toEntity(dto);
        entity.setOwner(owner);
        
        PropertyEntity saved = propertyRepository.save(entity);
        return propertyMapper.toDto(saved);
    }
}
