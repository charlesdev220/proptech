package com.proptech.backend.infrastructure.mapper;

import com.proptech.backend.api.dto.GeoJsonGeometry;
import com.proptech.backend.api.dto.NeighborhoodDTO;
import com.proptech.backend.infrastructure.persistence.entity.NeighborhoodEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class NeighborhoodMapper {

    @Mapping(target = "polygon", source = "geometry", qualifiedByName = "toGeoJson")
    public abstract NeighborhoodDTO toDto(NeighborhoodEntity entity);

    @Named("toGeoJson")
    public GeoJsonGeometry toGeoJson(MultiPolygon geometry) {
        if (geometry == null) return null;

        List<Object> polygonCoords = new ArrayList<>();
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) geometry.getGeometryN(i);
            List<Object> rings = new ArrayList<>();
            rings.add(ringToCoordList(polygon.getExteriorRing()));
            for (int h = 0; h < polygon.getNumInteriorRing(); h++) {
                rings.add(ringToCoordList(polygon.getInteriorRingN(h)));
            }
            polygonCoords.add(rings);
        }

        GeoJsonGeometry geo = new GeoJsonGeometry();
        geo.setType(GeoJsonGeometry.TypeEnum.MULTIPOLYGON);
        geo.setCoordinates(polygonCoords);
        return geo;
    }

    private List<List<Double>> ringToCoordList(LineString ring) {
        List<List<Double>> coords = new ArrayList<>();
        for (Coordinate c : ring.getCoordinates()) {
            coords.add(List.of(c.x, c.y));
        }
        return coords;
    }
}
