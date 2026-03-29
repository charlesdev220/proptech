package com.proptech.backend.infrastructure.persistence.seeder;

import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // 1. Create a mock owner
        UserEntity owner = new UserEntity();
        owner.setName("Juan Pérez");
        owner.setEmail("juan@example.com");
        owner.setPassword("{noop}admin123"); // {noop} for plain text with Spring Security
        owner.setTrustScore(85);
        owner.setIsVerified(true);
        userRepository.save(owner);

        // 2. Create some properties in Madrid center
        createProperty(owner, "Ático de lujo en Malasaña", "Increíble ático con terraza...", 1200, 40.4243, -3.7049, 2, 1, 85.0);
        createProperty(owner, "Estudio acogedor en Lavapiés", "Reformado, ideal parejas...", 850, 40.4087, -3.7003, 1, 1, 40.0);
        createProperty(owner, "Piso señorial en Salamanca", "Techos altos, portería...", 2500, 40.4289, -3.6844, 4, 3, 150.0);
    }

    private void createProperty(UserEntity owner, String title, String desc, double price, double lat, double lng, int rooms, int bathrooms, double surface) {
        Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
        PropertyEntity property = PropertyEntity.builder()
                .title(title)
                .description(desc)
                .price(BigDecimal.valueOf(price))
                .type("RENT")
                .location(point)
                .address("Madrid, España")
                .owner(owner)
                .rooms(rooms)
                .bathrooms(bathrooms)
                .surface(surface)
                .hasElevator(true)
                .hasParking(false)
                .energyCertificate("A")
                .build();
        propertyRepository.save(property);
    }
}
