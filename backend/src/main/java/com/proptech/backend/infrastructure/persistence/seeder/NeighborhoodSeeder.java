package com.proptech.backend.infrastructure.persistence.seeder;

import com.proptech.backend.infrastructure.persistence.entity.NeighborhoodEntity;
import com.proptech.backend.infrastructure.persistence.repository.NeighborhoodRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeighborhoodSeeder {

    private final NeighborhoodRepository neighborhoodRepository;

    @PostConstruct
    public void seed() {
        if (neighborhoodRepository.count() > 0) {
            return;
        }

        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

        List<NeighborhoodEntity> neighborhoods = List.of(
            buildNeighborhood(gf, "Malasaña",           -3.711, 40.421, -3.697, 40.430),
            buildNeighborhood(gf, "Lavapiés",           -3.707, 40.406, -3.695, 40.416),
            buildNeighborhood(gf, "Salamanca",          -3.686, 40.421, -3.668, 40.437),
            buildNeighborhood(gf, "Chamberí",           -3.703, 40.430, -3.685, 40.443),
            buildNeighborhood(gf, "Retiro",             -3.688, 40.407, -3.668, 40.422),
            buildNeighborhood(gf, "Chueca",             -3.699, 40.421, -3.691, 40.428),
            buildNeighborhood(gf, "Moncloa",            -3.728, 40.428, -3.710, 40.443),
            buildNeighborhood(gf, "Arganzuela",         -3.708, 40.393, -3.690, 40.408),
            buildNeighborhood(gf, "Centro",             -3.712, 40.411, -3.697, 40.421),
            buildNeighborhood(gf, "Hortaleza",          -3.645, 40.468, -3.605, 40.495),
            buildNeighborhood(gf, "Carabanchel",        -3.742, 40.380, -3.706, 40.400),
            buildNeighborhood(gf, "Vallecas",           -3.668, 40.390, -3.640, 40.412),
            buildNeighborhood(gf, "Vicálvaro",          -3.610, 40.395, -3.580, 40.420),
            buildNeighborhood(gf, "Moratalaz",          -3.660, 40.400, -3.635, 40.418),
            buildNeighborhood(gf, "Ciudad Lineal",      -3.665, 40.430, -3.630, 40.460),
            buildNeighborhood(gf, "Barajas",            -3.590, 40.455, -3.550, 40.485),
            buildNeighborhood(gf, "Fuencarral",         -3.710, 40.465, -3.660, 40.500),
            buildNeighborhood(gf, "Tetuán",             -3.706, 40.443, -3.685, 40.461),
            buildNeighborhood(gf, "Usera",              -3.718, 40.388, -3.695, 40.403),
            buildNeighborhood(gf, "Latina",             -3.740, 40.400, -3.712, 40.420),
            buildNeighborhood(gf, "Puente de Vallecas", -3.668, 40.400, -3.645, 40.420)
        );

        neighborhoodRepository.saveAll(neighborhoods);
        log.info("NeighborhoodSeeder: {} barrios de Madrid importados.", neighborhoods.size());
    }

    private NeighborhoodEntity buildNeighborhood(GeometryFactory gf, String name,
            double minLng, double minLat, double maxLng, double maxLat) {

        Coordinate[] coords = {
            new Coordinate(minLng, minLat),
            new Coordinate(maxLng, minLat),
            new Coordinate(maxLng, maxLat),
            new Coordinate(minLng, maxLat),
            new Coordinate(minLng, minLat)
        };
        LinearRing ring = gf.createLinearRing(coords);
        Polygon polygon = gf.createPolygon(ring);
        MultiPolygon mp = gf.createMultiPolygon(new Polygon[]{polygon});

        return NeighborhoodEntity.builder()
            .name(name)
            .geometry(mp)
            .build();
    }
}
