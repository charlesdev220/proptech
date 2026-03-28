# 📍 Historial de Implementación - PropTech Platform

> Registro secuencial de tareas completadas según el PropTech_Implementation_Plan.md y WBS. El orden es cronológico inverso (lo más reciente arriba).

### Qué hemos completado hasta ahora (Migración de Almacenamiento Media NoSQL/LOB):
*Fase actual:* Fase 1: MVP - 1.2 Core de Gestión de Inmuebles
*Estado actual:* Completado
- ✔️ **Almacenamiento Local (PostgreSQL)**: Integradas las capas Entity, Service y Controller REST para `multipart/form-data` y servir blobs serializados vía stream.
- ✔️ **Estrategia Compresión Frontend**: Aplicada lógica Nativa Canvas en Angular 17. Límite de resolución HD (1280px a 70% calidad). Video protegido a 15MB.

### Qué hemos completado hasta ahora (Motor de Publicación Fullstack):
*Fase actual:* Fase 1: MVP - 1.2 Core de Gestión de Inmuebles
*Estado actual:* En Proceso
- ✔️ **Contrato API (Contract-first):** Añadido el endpoint de subida de imágenes (`/api/v1/media/upload`) al Spec OpenAPI.
- ✔️ **Microservicio de gestión de medios (S3):** Creados el `MediaController` y `PropertyController` en Spring Boot (Implementación preliminar).
- ✔️ **Formulario reactivo en Angular:** Creado componente Standalone (`PropertyPublishComponent`) con Signals para la publicación íntegra.

### Qué hemos completado hasta ahora (Finalización de 1.1):
*Fase actual:* Fase 1: MVP - 1.1 Fundamentos de Infraestructura y Backend
*Estado actual:* Completado
- ✔️ **Pipeline CI/CD con GitHub Actions:** Creado flujo base en .github/workflows para backend y frontend.
- ✔️ **Entidades de DB y JWT:** Entidades core (User, Property, Listing) creadas con PostGIS. Módulo JWT base (JwtService, SecurityConfig) implementado en Spring Boot.

### Qué hemos completado hasta ahora (Boilerplate y Cimientos):
*Fase actual:* Fase 1: MVP - 1.1 Fundamentos de Infraestructura y Backend
*Estado actual:* En Proceso
- ✔️ **Contrato API (Contract-first):** Hemos definido la Especificación OpenAPI base `openapi.yaml`.
- ✔️ **Configuración de PostGIS:** El docker-compose.yml local y las propiedades en Spring Boot están listas.
- ✔️ **Estructura Base Spring Boot:** Todas las carpetas hexagonales y dependencias (MapStruct, JWT, JPA) están inicializadas.
- ✔️ **Estructura Base Angular:** Proyecto Standalone funcional, Tailwind configurado y las interfaces TypeScript generadas automáticamente desde el OpenAPI.
