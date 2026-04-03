package com.proptech.backend.infrastructure.persistence.seeder;

import com.proptech.backend.infrastructure.persistence.entity.MediaEntity;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.MediaRepository;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static final String[] IMAGE_URLS = {
        "https://loremflickr.com/800/600/apartment,exterior?lock=1",
        "https://loremflickr.com/800/600/apartment,interior?lock=2",
        "https://loremflickr.com/800/600/bedroom,modern?lock=3",
        "https://loremflickr.com/800/600/livingroom,apartment?lock=4",
        "https://loremflickr.com/800/600/kitchen,apartment?lock=5",
        "https://loremflickr.com/800/600/bathroom,apartment?lock=6",
        "https://loremflickr.com/800/600/apartment,balcony?lock=7",
        "https://loremflickr.com/800/600/apartment,building?lock=8",
        "https://loremflickr.com/800/600/house,exterior?lock=9",
        "https://loremflickr.com/800/600/penthouse?lock=10",
        "https://loremflickr.com/800/600/apartment,exterior?lock=11",
        "https://loremflickr.com/800/600/apartment,interior?lock=12",
        "https://loremflickr.com/800/600/bedroom,cozy?lock=13",
        "https://loremflickr.com/800/600/livingroom,modern?lock=14",
        "https://loremflickr.com/800/600/kitchen,modern?lock=15",
        "https://loremflickr.com/800/600/apartment,pool?lock=16",
        "https://loremflickr.com/800/600/apartment,city?lock=17",
        "https://loremflickr.com/800/600/condo?lock=18",
        "https://loremflickr.com/800/600/realestate,apartment?lock=19",
        "https://loremflickr.com/800/600/loft,apartment?lock=20"
    };

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        List<UserEntity> users = createUsers();
        createProperties(users);
    }

    private List<UserEntity> createUsers() {
        String[][] userData = {
            {"Juan Pérez",      "juan@example.com",    "85", "true"},
            {"María García",    "maria@example.com",   "72", "true"},
            {"Carlos López",    "carlos@example.com",  "60", "false"},
            {"Ana Martínez",    "ana@example.com",     "90", "true"},
            {"Pedro Sánchez",   "pedro@example.com",   "45", "false"},
            {"Laura Fernández", "laura@example.com",   "78", "true"},
            {"Javier Gómez",    "javier@example.com",  "55", "false"},
            {"Elena Díaz",      "elena@example.com",   "88", "true"},
            {"Miguel Torres",   "miguel@example.com",  "30", "false"},
            {"Sofía Ruiz",      "sofia@example.com",   "95", "true"},
            {"Diego Moreno",    "diego@example.com",   "65", "true"},
            {"Valentina Muñoz", "valentina@example.com","20", "false"},
            {"Andrés Álvarez",  "andres@example.com",  "70", "true"},
            {"Carmen Romero",   "carmen@example.com",  "82", "true"},
            {"Pablo Jiménez",   "pablo@example.com",   "40", "false"},
            {"Isabel Navarro",  "isabel@example.com",  "75", "true"},
            {"Fernando Molina", "fernando@example.com","50", "false"},
            {"Pilar Ortega",    "pilar@example.com",   "68", "true"},
            {"Rodrigo Castro",  "rodrigo@example.com", "35", "false"},
            {"Lucía Vega",      "lucia@example.com",   "92", "true"}
        };

        return userData.length > 0
            ? java.util.Arrays.stream(userData).map(d -> {
                UserEntity u = new UserEntity();
                u.setName(d[0]);
                u.setEmail(d[1]);
                u.setPassword("{noop}password123");
                u.setTrustScore(Integer.parseInt(d[2]));
                u.setIsVerified(Boolean.parseBoolean(d[3]));
                return userRepository.save(u);
            }).toList()
            : List.of();
    }

    private void createProperties(List<UserEntity> users) {
        // lat, lng, barrio
        double[][] malasana  = {{40.4252,-3.7049},{40.4243,-3.7065},{40.4261,-3.7031},{40.4238,-3.7055},{40.4270,-3.7040},{40.4248,-3.7070},{40.4255,-3.7025},{40.4232,-3.7045},{40.4265,-3.7060},{40.4241,-3.7035}};
        double[][] lavapies  = {{40.4087,-3.7003},{40.4075,-3.7015},{40.4099,-3.6995},{40.4083,-3.7025},{40.4092,-3.6985},{40.4071,-3.7008},{40.4105,-3.7002},{40.4079,-3.6990},{40.4095,-3.7018},{40.4068,-3.7012}};
        double[][] salamanca = {{40.4289,-3.6844},{40.4300,-3.6860},{40.4275,-3.6830},{40.4310,-3.6850},{40.4282,-3.6870},{40.4295,-3.6820},{40.4268,-3.6855},{40.4305,-3.6838},{40.4278,-3.6842},{40.4315,-3.6865}};
        double[][] chamberi  = {{40.4380,-3.7020},{40.4392,-3.7035},{40.4368,-3.7008},{40.4405,-3.7025},{40.4375,-3.7045},{40.4360,-3.7015},{40.4398,-3.7002},{40.4385,-3.7038},{40.4370,-3.7030},{40.4410,-3.7010}};
        double[][] retiro    = {{40.4150,-3.6830},{40.4163,-3.6845},{40.4138,-3.6818},{40.4172,-3.6835},{40.4145,-3.6855},{40.4130,-3.6825},{40.4168,-3.6810},{40.4155,-3.6840},{40.4142,-3.6850},{40.4178,-3.6820}};

        Object[][] propData = {
            // {title, desc, price, type, lat, lng, rooms, bathrooms, surface, hasElev, hasParking, energyCert, barrio}
            {"Ático de lujo",          "Terraza con vistas espectaculares",   1200, "RENT", malasana[0],  2,1,85.0,  true, false,"A","Malasaña"},
            {"Estudio moderno",        "Reformado, luz natural, ideal parejas", 850,"RENT", lavapies[0],  1,1,40.0, false, false,"C","Lavapiés"},
            {"Piso señorial",          "Techos altos, portería 24h",          2500, "RENT", salamanca[0], 4,3,150.0, true,  true,"B","Salamanca"},
            {"Piso acogedor",          "Cerca del metro, bien comunicado",     950, "RENT", chamberi[0],  2,1,65.0,  true, false,"C","Chamberí"},
            {"Apartamento con jardín", "Planta baja con jardín privado",      1800, "SALE", retiro[0],    3,2,100.0, false, true,"B","Retiro"},
            {"Loft industrial",        "Espacio diáfano con mezzanine",       1400, "RENT", malasana[1],  1,1,70.0, false, false,"D","Malasaña"},
            {"Piso luminoso",          "Orientación sur, balcón amplio",      1100, "RENT", lavapies[1],  2,1,58.0,  true, false,"C","Lavapiés"},
            {"Casa adosada",           "3 plantas, garage y patio",          320000,"SALE", salamanca[1], 4,3,180.0, false, true,"A","Salamanca"},
            {"Estudio coqueto",        "Amueblado, lista para entrar",         750, "RENT", chamberi[1],  1,1,35.0, false, false,"D","Chamberí"},
            {"Ático dúplex",           "Dos plantas, terraza privada",        2200, "RENT", retiro[1],    3,2,120.0, true,  true,"A","Retiro"},
            {"Piso en reforma",        "Gran oportunidad inversión",         185000,"SALE", malasana[2],  3,2,90.0, false, false,"F","Malasaña"},
            {"Apartamento nuevo",      "Estreno, acabados de lujo",           1350, "RENT", lavapies[2],  2,2,75.0,  true, false,"A","Lavapiés"},
            {"Piso familiar",          "Colegio a 200m, muy tranquilo",      1600, "RENT", salamanca[2],  3,2,110.0, true,  true,"B","Salamanca"},
            {"Bajo con terraza",       "Terraza de 30m², muy verde",           980, "RENT", chamberi[2],  2,1,55.0, false, false,"C","Chamberí"},
            {"Piso clásico",           "Parquet, mucha luz, muy cuidado",     1250, "RENT", retiro[2],    3,1,80.0,  true, false,"C","Retiro"},
            {"Estudio premium",        "Vistas al parque, concierge",          900, "RENT", malasana[3],  1,1,42.0,  true, false,"B","Malasaña"},
            {"Piso compartido",        "Habitación en piso de 4",              600, "RENT", lavapies[3],  1,1,20.0, false, false,"D","Lavapiés"},
            {"Chalet adosado",         "Urbanización privada, piscina",      450000,"SALE", salamanca[3], 5,4,250.0, false, true,"A","Salamanca"},
            {"Piso con trastero",      "Amplio, trastero incluido",           1150, "RENT", chamberi[3],  2,1,68.0,  true, false,"C","Chamberí"},
            {"Apartamento ejecutivo",  "Business district, gym incluido",     1900, "RENT", retiro[3],    2,2,85.0,  true,  true,"A","Retiro"},
            {"Piso artístico",         "Zona de galerías, lleno de vida",     1050, "RENT", malasana[4],  2,1,60.0, false, false,"C","Malasaña"},
            {"Estudio con altillo",    "Diseño original, bien aprovechado",    820, "RENT", lavapies[4],  1,1,38.0, false, false,"D","Lavapiés"},
            {"Piso de lujo",           "Domótica, materiales premium",        3200, "RENT", salamanca[4], 4,3,180.0, true,  true,"A","Salamanca"},
            {"Piso reformado",         "Cocina de diseño, baños nuevos",      1050, "RENT", chamberi[4],  2,2,70.0,  true, false,"B","Chamberí"},
            {"Villa urbana",           "Jardín propio en plena ciudad",      520000,"SALE", retiro[4],    5,4,300.0, false, true,"A","Retiro"},
            {"Piso con historia",      "Edificio modernista, muy único",      1300, "RENT", malasana[5],  3,1,85.0, false, false,"C","Malasaña"},
            {"Apartamento tranquilo",  "Interior, muy silencioso",             780, "RENT", lavapies[5],  1,1,40.0, false, false,"D","Lavapiés"},
            {"Piso ejecutivo",         "A/C, parking, zona prime",            2800, "RENT", salamanca[5], 3,2,130.0, true,  true,"A","Salamanca"},
            {"Bajo reformado",         "Salida a patio comunitario",           920, "RENT", chamberi[5],  2,1,52.0, false, false,"C","Chamberí"},
            {"Piso con galería",       "Galería acristalada, muy cálido",     1400, "RENT", retiro[5],    3,2,95.0,  true, false,"B","Retiro"},
            {"Apartamento bohemio",    "Arte urbano, zona muy animada",        990, "RENT", malasana[6],  2,1,55.0, false, false,"C","Malasaña"},
            {"Piso soleado",           "5 plantas, ascensor, portería",       1180, "RENT", lavapies[6],  2,1,65.0,  true, false,"B","Lavapiés"},
            {"Piso clásico amplio",    "Salón de 40m², muy elegante",        2100, "RENT", salamanca[6],  3,2,120.0, true, false,"B","Salamanca"},
            {"Estudio funcional",      "Bien distribuido, cocina americana",   700, "RENT", chamberi[6],  1,1,32.0, false, false,"D","Chamberí"},
            {"Piso con vistas",        "10ª planta, vistas despejadas",       1700, "RENT", retiro[6],    3,2,100.0, true,  true,"A","Retiro"},
            {"Piso hipster",           "Ladrillo visto, vigas de madera",     1080, "RENT", malasana[7],  2,1,60.0, false, false,"C","Malasaña"},
            {"Piso económico",         "Buen precio, buena zona",              680, "RENT", lavapies[7],  1,1,38.0, false, false,"E","Lavapiés"},
            {"Piso de representación", "Reuniones, despachos, muy serio",    3800, "RENT", salamanca[7],  4,3,200.0, true,  true,"A","Salamanca"},
            {"Piso con parking",       "Parking y trastero incluidos",        1280, "RENT", chamberi[7],  2,2,75.0,  true,  true,"B","Chamberí"},
            {"Loft de diseño",         "Interiorismo de firma, exclusivo",    2400, "RENT", retiro[7],    2,2,110.0, true,  true,"A","Retiro"},
            {"Apartamento familiar",   "Parque en la puerta, colegios",       1120, "RENT", malasana[8],  3,1,75.0,  true, false,"C","Malasaña"},
            {"Estudio minimalista",    "Muebles de diseño incluidos",          860, "RENT", lavapies[8],  1,1,40.0, false, false,"C","Lavapiés"},
            {"Piso ático último",      "Terraza de 80m², barbacoa",          2900, "RENT", salamanca[8],  3,2,140.0, true,  true,"A","Salamanca"},
            {"Piso tipo loft",         "Espacios abiertos, muy creativo",     1050, "RENT", chamberi[8],  2,1,65.0, false, false,"C","Chamberí"},
            {"Piso con jardín",        "Jardín comunitario 500m²",            1600, "RENT", retiro[8],    3,2,100.0,  true, false,"B","Retiro"},
            {"Piso vintage",           "Años 60 restaurado con gusto",        1200, "RENT", malasana[9],  2,1,70.0, false, false,"C","Malasaña"},
            {"Piso multiusos",         "Posibilidad uso mixto residencial",    940, "RENT", lavapies[9],  2,1,55.0, false, false,"D","Lavapiés"},
            {"Piso gourmet",           "Cocina de chef, perfecta",           2600, "RENT", salamanca[9],  3,2,130.0, true,  true,"A","Salamanca"},
            {"Piso compacto premium",  "Cada cm² bien aprovechado",           880, "RENT", chamberi[9],  1,1,45.0,  true, false,"B","Chamberí"},
            {"Gran piso familiar",     "Ideal familia numerosa, luminoso",   2000, "RENT", retiro[9],    4,3,160.0,  true,  true,"B","Retiro"}
        };

        for (int i = 0; i < propData.length; i++) {
            Object[] d = propData[i];
            double[] coords = (double[]) d[4];
            UserEntity owner = users.get(i % users.size());

            Point point = geometryFactory.createPoint(new Coordinate(coords[1], coords[0]));
            PropertyEntity property = PropertyEntity.builder()
                    .title((String) d[0])
                    .description((String) d[1])
                    .price(BigDecimal.valueOf((int) d[2]))
                    .type((String) d[3])
                    .location(point)
                    .address("Madrid, " + d[12])
                    .owner(owner)
                    .rooms((int) d[5])
                    .bathrooms((int) d[6])
                    .surface((double) d[7])
                    .hasElevator((boolean) d[8])
                    .hasParking((boolean) d[9])
                    .energyCertificate((String) d[10])
                    .build();
            PropertyEntity saved = propertyRepository.save(property);
            attachImages(saved, i);
        }
    }

    /** Assigns 3 images per property cycling through the URL pool. */
    private void attachImages(PropertyEntity property, int propertyIndex) {
        HttpClient http = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        for (int slot = 0; slot < 3; slot++) {
            int urlIndex = (propertyIndex * 3 + slot) % IMAGE_URLS.length;
            String url = IMAGE_URLS[urlIndex];
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build();

                HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() == 200) {
                    byte[] imageData = response.body();
                    String contentType = response.headers()
                            .firstValue("content-type")
                            .orElse("image/jpeg");

                    MediaEntity media = new MediaEntity();
                    media.setFileName("property-" + propertyIndex + "-img" + slot + ".jpg");
                    media.setContentType(contentType);
                    media.setData(imageData);
                    media.setSize((long) imageData.length);
                    media.setProperty(property);
                    mediaRepository.save(media);
                }
            } catch (IOException | InterruptedException e) {
                log.warn("No se pudo descargar imagen {} para propiedad {}: {}", url, property.getId(), e.getMessage());
            }
        }
    }
}
