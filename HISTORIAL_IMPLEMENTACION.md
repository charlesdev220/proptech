# 📍 Historial de Implementación - PropTech Platform

> Registro secuencial de tareas completadas según el PropTech_Implementation_Plan.md y WBS. El orden es cronológico inverso (lo más reciente arriba).

### Qué hemos completado hasta ahora (Módulo de Carga Segura KYC):
*Fase actual:* Fase 1: MVP - 1.3 Perfil de Usuario y Reputación
*Estado actual:* Completado (Frontend & Contract)
- ✔️ **Contract-First (OpenAPI)**: Añadido el endpoint POST `/profile/documents` dedicado exclusivamente a la verificación de KYC (DNI/Nóminas), regenerando la API en Angular.
- ✔️ **Frontend (DocumentUploader)**: Creado `DocumentUploaderComponent` (Standalone + Signals) con validaciones de tamaño (5MB) e interfaz intuitiva Drag&Drop-like para la selección de archivos locales.
- ✔️ **Integración SDD**: Integrado el componente uploader de documentos como Widget en el Dashboard Principal (`user-dashboard.ts`), enlazando el evento de éxito con la actualización general del Trust Score.


### Qué hemos completado hasta ahora (Dashboard de Perfil y Trust Score):
*Fase actual:* Fase 1: MVP - 1.3 Perfil de Usuario y Reputación
*Estado actual:* Completado (Frontend & Integration)
- ✔️ **Frontend (Dashboard)**: Creado `UserProfileComponent` utilizando Signals y componentes Standalone de Angular 17.
- ✔️ **Diseño Premium**: Implementados gradientes dinámicos y badges visuales para los niveles de reputación (Bronze/Gold/Platinum).
- ✔️ **Navegación**: Registrada la ruta `/profile` en `app.routes.ts` con carga perezosa (Lazy Loading).
- ✔️ **Consumo de API**: Integradas las llamadas a `PerfilService` para obtener el perfil y el desglose del score en una sola vista.


### Qué hemos completado hasta ahora (Remediación de Seguridad y Sincronización):
*Fase actual:* Fase 1: MVP - 1.3 Perfil de Usuario y Reputación
*Estado actual:* Completado (Pushed to GitHub)
- ✔️ **Sanitización de Seguridad**: Eliminado Mapbox Secret Token del código fuente y movido a variable de entorno/placeholder para cumplir con GitHub Push Protection.
- ✔️ **Remediación de Historial Git**: Reescrito el historial local (`git reset --soft` y `amend`) para purgar el secreto de los commits antes del push exitoso.
- ✔️ **Sincronización de Perfil**: Confirmado el flujo de extremo a extremo para las APIs de scoring y perfil de usuario.

### Qué hemos completado hasta ahora (Gestión de Perfiles y Trust Scoring):
*Fase actual:* Fase 1: MVP - 1.3 Perfil de Usuario y Reputación
*Estado actual:* En Proceso (Backend 100% Funcional y API Sincronizada)
*Próximos pasos:* Fase 1.3 - Implementar Dashboard de Perfil en Angular con Visualización de Scoring.
- ✔️ **Backend (Scoring Logic)**: Implementado `ScoringService` con lógica por factores (Emails, Verificaciones) y niveles (Bronze/Gold/Platinum).
- ✔️ **Profile API (Contract-First)**: Expuestos endpoints `/profile` y `/profile/trust-score` en `ProfileController`. 
- ✔️ **Arquitectura de Sincronización**: Configurado `openapi-generator-maven-plugin` en el backend para DTOs autogenerados, eliminando mocks manuales.
- ✔️ **Frontend (API Generation)**: Actualizados los servicios TypeScript de cliente mediante `npm run generate:api` sincronizados con el nuevo contrato.

### Qué hemos completado hasta ahora (Motor de Búsqueda Geoespacial):
*Fase actual:* Fase 1: MVP - 1.2 Core de Gestión de Inmuebles
*Estado actual:* Completado
*Próximos pasos:* Fase 1.3 - Perfil de Usuario, Scoring Inicial v1 y Módulo de Carga de Documentos Segura.
- ✔️ **Backend (PostGIS Query)**: Implementada búsqueda espacial en `PropertyRepository` usando `ST_DWithin` y MapStruct para tipos JTS `Point`.
- ✔️ **Frontend (Mapbox Integration)**: Creado `PropertyListComponent` (Standalone) con integración de Mapbox GL JS, marcadores dinámicos y filtros reactivos (Signals).
- ✔️ **Contrato API**: Ampliado `openapi.yaml` con parámetros de lat/lng/radio y nuevas interfaces generadas.

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
