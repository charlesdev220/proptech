package com.proptech.backend.infrastructure.mapper;

import com.proptech.backend.api.dto.MediaDTO;
import com.proptech.backend.api.dto.PropertyCreateDTO;
import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.api.dto.PropertyDTOLocation;
import com.proptech.backend.infrastructure.persistence.entity.MediaEntity;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Mapping(target = "location", source = "entity", qualifiedByName = "toLocationDto")
    @Mapping(target = "thumbnailUrl", source = "mediaFiles", qualifiedByName = "toThumbnailUrl")
    PropertyDTO toDto(PropertyEntity entity);

    @Mapping(target = "location", source = "entity", qualifiedByName = "toLocationDto")
    @Mapping(target = "fullAddress", source = "address")
    @Mapping(target = "mediaPreviews", source = "mediaFiles", qualifiedByName = "toMediaDtoList")
    @Mapping(target = "features.rooms", source = "rooms")
    @Mapping(target = "features.bathrooms", source = "bathrooms")
    @Mapping(target = "features.surface", source = "surface")
    @Mapping(target = "features.hasElevator", source = "hasElevator")
    @Mapping(target = "features.hasParking", source = "hasParking")
    @Mapping(target = "features.energyCertificate", source = "energyCertificate")
    @Mapping(target = "minSolvencyScore", source = "minSolvencyScore")
    com.proptech.backend.api.dto.PropertyDetailDTO toDetailDto(PropertyEntity entity);

    @Mapping(target = "location", source = "dto.location", qualifiedByName = "toPoint")
    @Mapping(target = "address", source = "dto.location.address")
    @Mapping(target = "rooms", source = "dto.features.rooms")
    @Mapping(target = "bathrooms", source = "dto.features.bathrooms")
    @Mapping(target = "surface", source = "dto.features.surface")
    @Mapping(target = "hasElevator", source = "dto.features.hasElevator")
    @Mapping(target = "hasParking", source = "dto.features.hasParking")
    @Mapping(target = "energyCertificate", source = "dto.features.energyCertificate")
    @Mapping(target = "minSolvencyScore", source = "dto.minSolvencyScore")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "mediaFiles", ignore = true)
    PropertyEntity toEntity(PropertyCreateDTO dto);

    @Named("toLocationDto")
    default PropertyDTOLocation toLocationDto(PropertyEntity entity) {
        if (entity.getLocation() == null) return null;
        PropertyDTOLocation loc = new PropertyDTOLocation();
        loc.setLatitude(BigDecimal.valueOf(entity.getLocation().getY()));
        loc.setLongitude(BigDecimal.valueOf(entity.getLocation().getX()));
        loc.setAddress(entity.getAddress());
        return loc;
    }

    @Named("toPoint")
    default Point toPoint(PropertyDTOLocation locationDto) {
        if (locationDto == null || locationDto.getLatitude() == null || locationDto.getLongitude() == null) {
            return null;
        }
        return GEOMETRY_FACTORY.createPoint(new Coordinate(
            locationDto.getLongitude().doubleValue(),
            locationDto.getLatitude().doubleValue()
        ));
    }

    @Named("toThumbnailUrl")
    default String toThumbnailUrl(java.util.List<MediaEntity> mediaFiles) {
        if (mediaFiles == null || mediaFiles.isEmpty()) return null;
        return "/api/v1/media/" + mediaFiles.get(0).getId();
    }

    @Named("toMediaDtoList")
    default java.util.List<MediaDTO> toMediaDtoList(java.util.List<MediaEntity> mediaFiles) {
        if (mediaFiles == null) return java.util.List.of();
        return mediaFiles.stream().map(this::toMediaDto).toList();
    }

    default MediaDTO toMediaDto(MediaEntity entity) {
        if (entity == null) return null;
        MediaDTO dto = new MediaDTO();
        dto.setUrl(URI.create("/api/v1/media/" + entity.getId()));
        dto.setFileName(entity.getFileName());
        dto.setSize(entity.getSize());
        return dto;
    }

    default OffsetDateTime mapDateTime(LocalDateTime value) {
        if (value == null) return null;
        return value.atOffset(ZoneOffset.UTC);
    }
}
