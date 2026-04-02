# 📍 Historial de Implementación - PropTech Platform

> Registro secuencial de tareas completadas según el PropTech_Implementation_Plan.md y WBS. El orden es cronológico inverso (lo más reciente arriba).

### Qué hemos completado hasta ahora (Go-Live Localizado — Beta 100 Usuarios):
*Fase actual:* Fase 1: MVP - 1.3 Perfiles y Lanzamiento Beta
*Estado actual:* Completado
- ✔️ **Security Hardening**: `POST /properties` y `POST /profile/documents` ahora requieren autenticación JWT — eliminada brecha de acceso anónimo en `SecurityConfig`.
- ✔️ **GlobalExceptionHandler**: `@RestControllerAdvice` con `ProblemDetail` (RFC 7807) para 404, 400, 403 y 500 — ningún stacktrace Java llega al cliente.
- ✔️ **POST /profile/documents**: Endpoint KYC implementado en `ProfileController` con validación de tipo (JPEG/PNG/PDF) y límite de 5MB; delega a `MediaService` existente.
- ✔️ **DataSeeder Beta**: Ampliado a 20 usuarios con TrustScore variado (20–95) y 50 propiedades distribuidas en 5 barrios de Madrid (Malasaña, Lavapiés, Salamanca, Chamberí, Retiro).
- ✔️ **Stress Testing k6**: Scripts `smoke-login.js`, `smoke-properties.js`, `smoke-trust-score.js` + orquestador `smoke-all.sh`; umbrales p95 < 800ms y error rate < 2% a 50 VUs.
- ✔️ **CI/CD Tests**: GitHub Actions ejecuta `mvn test` (backend) y `ng test --watch=false --browsers=ChromeHeadless` (frontend) en cada PR — tests rotos bloquean merge.
- ✔️ **Tests Unitarios**: `GlobalExceptionHandlerTest` (4 scenarios) + `ProfileControllerDocumentTest` (4 scenarios) verificando REQ-02 y REQ-03.
*Próximos pasos:* Fase 2 — KYC biométrico (Onfido/Veriff), Scoring v2 y Motor de Reputación Bidireccional.

### Qué hemos completado hasta ahora (Módulo de Autenticación JWT Full-Stack):
*Fase actual:* Fase 1: MVP - 1.3 Perfiles y Lanzamiento Beta
*Estado actual:* Completado (Full-Stack)
- ✔️ **Contrato API (Contract-First)**: Añadido endpoint `POST /auth/register` y schema `RegisterRequest` al OpenAPI.
- ✔️ **Backend - AuthController**: Refactorizado eliminando el doble mapping conflictivo con `AuthApi`. Ahora expone `POST /api/v1/auth/login` y `POST /api/v1/auth/register` bajo `@RequestMapping("/api/v1/auth")`.
- ✔️ **Backend - PasswordEncoder**: Añadido bean `DelegatingPasswordEncoder` en `SecurityConfig` (soporta `{noop}` del seeder de dev y BCrypt para nuevos registros).
- ✔️ **Backend - CustomUserDetailsService + JwtAuthFilter**: Integrados en el filter chain de Spring Security para validar tokens JWT en cada request.
- ✔️ **Frontend - AuthService**: Servicio standalone con signals (`isLoggedIn`) y métodos `login()`, `register()`, `logout()`. Persiste el token en `localStorage`.
- ✔️ **Frontend - AuthInterceptor**: Interceptor funcional (`HttpInterceptorFn`) que adjunta `Authorization: Bearer <token>` a todas las peticiones HTTP.
- ✔️ **Frontend - LoginComponent**: Componente standalone con tabs Login/Registro, validaciones de formulario reactivo y navegación post-autenticación.
- ✔️ **Frontend - Routing y Config**: Añadidas rutas `/login` y `/publish`. Registrado el interceptor vía `provideHttpClient(withInterceptors([authInterceptor]))`.
- ✔️ **Frontend - Navbar**: Botón "Login" conectado a `/login`; cambia a "Cerrar sesión" cuando el usuario está autenticado (reactivo por Signal).
*Próximos pasos:* Go-Live Localizado - Pruebas de carga (Stress Testing) y onboarding de primeros usuarios beta.

### Qué hemos completado hasta ahora (Migración de Mapbox a Leaflet):
*Fase actual:* Fase 1: MVP - 1.2 Core de Gestión de Inmuebles
*Estado actual:* Completado
- ✔️ **Frontend (Mapa Leaflet)**: Migrado el motor de mapas de Mapbox GL JS a Leaflet con OpenStreetMap para eliminar la dependencia de tokens y reducir el tamaño del bundle.
- ✔️ **Refactorización de Componentes**: Actualizado `PropertyListComponent` para usar `ngx-leaflet` y lógica de marcadores/eventos de Leaflet. 
- ✔️ **Configuración de Entorno**: Eliminado el secreto `MAPBOX_TOKEN` tanto de los scripts de construcción como del archivo `.env.template`.
- ✔️ **Optimización de Estilos**: Limpieza de imports CSS redundantes y configuración centralizada de estilos de mapas en `angular.json`.
*Próximos pasos:* Continuar con el flujo de publicación de inmuebles y gestión de medios en S3.

### Qué hemos completado hasta ahora (Ficha de Detalle de Inmueble):
*Fase actual:* Fase 1: MVP - 1.2 Core de Gestión de Inmuebles
*Estado actual:* Completado (Full-Stack)
- ✔️ **Contract-First (OpenAPI)**: Ampliado el contrato con `GET /properties/{id}` y `GET /properties/{id}/media`, definiendo `PropertyDetailDTO` con características técnicas (habitaciones, baños, superficie).
- ✔️ **Backend (Data & Logic)**: Actualizada `PropertyEntity` con campos de características y galería multimedia. Implementada la lógica en `PropertyService` y `PropertyController` (Lombok & Hexagonal).
- ✔️ **Frontend (UI Premium)**: Creado `PropertyDetailComponent` (Signals + Standalone) con diseño inmersivo, galería de imágenes, desglose técnico y visualización del Trust Score del propietario.
- ✔️ **Navegación**: Enlazada la lista de búsqueda con la vista de detalle mediante `RouterLink` y parámetros de ruta.


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
- ✔️ **Almacenamiento Local (PostgreSQL):** Migrado el almacenamiento de medios desde S3 a PostgreSQL con soporte para blobs (Large Objects). Esto incluye la integración de capas Entity, Service y Controller REST para manejar `multipart/form-data` y servir blobs serializados vía stream.
- ✔️ **Estrategia Compresión Frontend:** Aplicada lógica Nativa Canvas en Angular 17. Límite de resolución HD (1280px a 70% calidad). Video protegido a 15MB.
- **Motivación del Cambio:**
  - Reducción de dependencias externas (eliminación de S3).
  - Optimización de costos al reutilizar PostgreSQL como base de datos principal.
  - Mayor control sobre los datos almacenados.

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
