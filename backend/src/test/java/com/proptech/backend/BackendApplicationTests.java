package com.proptech.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere PostgreSQL activo. Ejecutar manualmente con 'docker-compose up' en entorno local.")
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
