---
name: mock-data-seeder
description: Automatización de poblado de datos (seeding) con Java Faker o SQL para generar entornos realistas durante Dev/Staging.
---

# Mock Data Seeder (Java Faker / SQL)

Instrucciones para crear y ejecutar scripts de inserción de datos masivos orientados al entorno PropTech.

## When to use this skill

- Utiliza esta habilidad cuando necesites probar el Frontend Angular y detectar regresiones de rendimiento, como tiempos de carga de la interfaz de usuario (UI) o problemas de N+1 queries.
- Esto es útil para simular escenarios realistas de uso en los entornos de desarrollo o pruebas antes de probar nuevas vistas.

## How to use it

1. **Uso de Librería:** Implementa la inyección de datos poblacionales mediante un `@Service` marcado con la anotación `@Profile("dev")` o ejecuta scripts de inserciones SQL puras en la base de datos de test.
2. **Propósito y Volumen:** Debes generar un ambiente que cumpla como mínimo con los siguientes requerimientos de volumen:
   - 10 Usuarios "Propietarios" con nivel 'Verificado Plata'.
   - 5 Usuarios "Buscadores" sin documentación.
   - 500 Inmuebles, asegurando que todos incluyan localizaciones GeoJSON / WKT situadas en el área delimitada de Madrid centro (esencial para usar renderizado mediante MapBox en Angular).
   - Datos simulados para valoraciones de usuario y revisiones.
3. **Invocación:** Cuando se solicite estructurar un script de relleno, utiliza habitualmente un `CommandLineRunner` (en Java) que se ejecute solo si está activado el perfil condicional correcto del entorno de pruebas.
