---
name: Mock Data Seeder (Java Faker / SQL)
description: Automatización para generar entornos poblados y realistas durante Dev/Staging.
---

# Skill: PropTech Data Seeder

Para probar el Frontend Angular y detectar regresiones de carga (Problema UI o N+1 Queries Paginadas):

1. **Uso de Librería:** Mediante un `@Service` marcado con un `@Profile("dev")` o mediante inserciones SQL puras.
2. **Propósito:** Generar un ambiente con al menos:
   - 10 Usuarios Propietarios con nivel 'Verificado Plata'.
   - 5 Usuarios Buscadores sin documentación.
   - 500 Inmuebles, todos con localizaciones GeoJSON / WKT en el área delimitada de Madrid centro (Para usar MapBox en Angular).
   - Datos simulados de valoraciones de usuario y revisiones. 
3. **Invocación:** El agente orquestador te pedirá que escribas este script de relleno (Seed file) si ve necesario hacer pruebas de carga, usualmente utilizando un `CommandLineRunner` (Java) inicializado bajo condicional de entorno.
