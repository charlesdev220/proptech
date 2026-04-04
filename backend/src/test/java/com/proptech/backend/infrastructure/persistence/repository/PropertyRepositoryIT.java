package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@Transactional
class PropertyRepositoryIT {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {
        @org.springframework.context.annotation.Bean
        public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
            return new com.fasterxml.jackson.databind.ObjectMapper();
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:15-3.3").asCompatibleSubstituteFor("postgres")
    )
            .withDatabaseName("proptech")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private EntityManager entityManager;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private UserEntity owner;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        
        owner = new UserEntity();
        owner.setEmail("owner@example.com");
        owner.setPassword("pass");
        owner.setName("Owner Name");
        entityManager.persist(owner);

        // 1. Inside Madrid Centro (Sol)
        PropertyEntity p1 = PropertyEntity.builder()
                .title("Apartamento en Sol")
                .description("Cerca del Km 0")
                .price(BigDecimal.valueOf(1500))
                .type("APARTMENT")
                .location(createPoint(-3.7038, 40.4168))
                .owner(owner)
                .build();

        // 2. Inside Madrid Centro (Chueca)
        PropertyEntity p2 = PropertyEntity.builder()
                .title("Piso en Chueca")
                .description("Moderno")
                .price(BigDecimal.valueOf(1800))
                .type("APARTMENT")
                .location(createPoint(-3.6996, 40.4225))
                .owner(owner)
                .build();

        // 3. Outside (Pozuelo)
        PropertyEntity p3 = PropertyEntity.builder()
                .title("Chalet en Pozuelo")
                .description("Lujo fuera del centro")
                .price(BigDecimal.valueOf(3500))
                .type("HOUSE")
                .location(createPoint(-3.8142, 40.4357))
                .owner(owner)
                .build();

        propertyRepository.saveAll(List.of(p1, p2, p3));
        entityManager.flush();
    }

    @Test
    void searchWithPolygon_returnsOnlyPropertiesInside() {
        // Madrid Centro Polygon (approx)
        String madridCentroPolygon = "{\"type\":\"Polygon\",\"coordinates\":[[[-3.715,40.410],[-3.690,40.410],[-3.690,40.430],[-3.715,40.430],[-3.715,40.410]]]}";

        Page<PropertyEntity> results = propertyRepository.searchWithPolygon(
                madridCentroPolygon, null, null, null, PageRequest.of(0, 10)
        );

        assertEquals(2, results.getContent().size(), "Debería encontrar 2 propiedades en Madrid Centro");
    }

    private Point createPoint(double lon, double lat) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
