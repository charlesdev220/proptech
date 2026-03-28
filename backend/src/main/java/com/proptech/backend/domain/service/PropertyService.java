package com.proptech.backend.domain.service;

import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class PropertyService {

    @Transactional
    public Map<String, Object> createProperty(Map<String, Object> propertyDto) {
        // FIXME: Replace with actual MapStruct Mapper and Repository save
        // PropertyEntity entity = propertyMapper.toEntity(propertyDto);
        // propertyRepository.save(entity);
        
        propertyDto.put("id", UUID.randomUUID().toString());
        propertyDto.put("status", "PUBLISHED");
        return propertyDto;
    }
}
