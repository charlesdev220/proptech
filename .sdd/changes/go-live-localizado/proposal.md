# Proposal: Go-Live Localizado (Beta 100 Usuarios)

## Intent

Cerrar la Fase 1.3 del MVP dejando el sistema listo para recibir los primeros 100 usuarios beta en un entorno localizado (Madrid). El objetivo no es producción a escala, sino un beta controlado con datos realistas, seguridad corregida y capacidad de carga validada.

## Scope

**DENTRO del cambio:**
- Corrección de brecha de seguridad: `POST /properties` requiere autenticación
- Implementación de `POST /profile/documents` en `ProfileController` (endpoint contratado pero ausente)
- `@ControllerAdvice` global con `ProblemDetail` (RFC 7807) para manejo centralizado de excepciones
- Stress testing con **k6**: escenarios para los 3 endpoints críticos (`POST /auth/login`, `GET /properties`, `GET /profile/trust-score`)
- Seed de onboarding: ampliar `DataSeeder` a 20 usuarios + 50 propiedades distribuidas en Madrid con datos realistas
- CI/CD: agregar ejecución de tests (`mvn test` + `ng test --watch=false`) al pipeline de GitHub Actions

**FUERA del cambio:**
- Docker / Kubernetes / staging deploy (Fase 2)
- Cobertura Jacoco 70% / SonarQube (Fase 2)
- Rate limiting / brute-force protection (Fase 2)
- Monitoring Prometheus/Grafana (Fase 2)
- Feature flags / sistema de invitaciones (Fase 2)
- Índices PostGIS explícitos vía Liquibase (Fase 2)

## Affected Areas

| Area | Layer | Impact |
|------|-------|--------|
| `SecurityConfig.java` | Backend / Infra | **High** — corrige autorización en POST /properties |
| `ProfileController.java` | Backend / API | **High** — implementa endpoint KYC faltante |
| `GlobalExceptionHandler.java` (nuevo) | Backend / API | **High** — RFC 7807, oculta stacktraces |
| `DataSeeder.java` | Backend / Infra | **Medium** — 20 users + 50 properties para beta |
| `k6/` (nuevos scripts) | Infra / Testing | **Medium** — escenarios de carga |
| `.github/workflows/ci.yml` | CI/CD | **Medium** — agrega ejecución de tests |

## Approach

**Go-Live por Capas** — tres bloques secuenciales en orden de criticidad:

1. **Bloque A — Hardening de Seguridad y Estabilidad** (primero, sin esto no hay beta)
   - Fix `SecurityConfig` + implementar `/profile/documents` + `@ControllerAdvice`

2. **Bloque B — Stress Testing** (valida que el sistema aguante carga básica)
   - Scripts k6: login flood, property search con coordenadas, trust-score burst
   - Umbral de aceptación: p95 < 800ms, error rate < 2% a 50 usuarios concurrentes

3. **Bloque C — Onboarding Data + CI** (habilita que los beta testers tengan algo con qué interactuar)
   - Ampliar seeder con datos realistas variados
   - CI ejecuta tests automáticamente

**Por qué no el Enfoque 2 (completo):** overkill para 100 usuarios. Docker, K8s y 70% coverage son metas de producción real, no de beta cerrado.

## Architecture Decisions

- **ADR-01:** Usar k6 (no Gatling/JMeter) → JavaScript nativo, sin JVM extra, fácil de versionar en el repo como scripts JS. Corre en CI sin setup adicional.
- **ADR-02:** `@RestControllerAdvice` con Spring's `ProblemDetail` (nativo desde Spring 6) → sin dependencias extra, ya en el classpath de Spring Boot 4.
- **ADR-03:** Ampliar `DataSeeder` existente (no crear nuevo) → mantiene el patrón `@Profile("dev")` ya establecido en el proyecto.
- **ADR-04:** CI agrega `mvn test` sin `-DskipTests` → los 2 tests existentes deben pasar; es el piso mínimo antes del beta.

## Risks

- **Geospatial queries lentas bajo carga:** `ST_DWithin` sin índice GIST explícito puede ser el cuello de botella. Mitigación: k6 lo detectará; si los tiempos superan umbral, se agrega índice inline (sin Liquibase en este cambio).
- **DataSeeder en prod accidental:** el perfil `dev` ya protege esto, pero hay que validar que staging no cargue el perfil dev.
- **Tests existentes rotos:** `PropertyControllerTest` está incompleto — podría fallar al ejecutarse en CI. Mitigación: arreglar o skipear con `@Disabled` documentado antes de activar CI tests.

## Definition of Done

- [ ] `POST /properties` devuelve `401` sin token JWT válido
- [ ] `POST /profile/documents` acepta multipart y retorna `201` con `MediaDTO`
- [ ] Cualquier excepción no controlada devuelve `ProblemDetail` JSON (sin stacktrace)
- [ ] k6 smoke test pasa: p95 < 800ms y error rate < 2% a 50 VUs durante 30s
- [ ] `DataSeeder` carga 20 usuarios + 50 propiedades al arrancar con perfil `dev`
- [ ] GitHub Actions ejecuta `mvn test` y `ng test --watch=false` en cada PR
- [ ] Todos los tests existentes pasan en CI (o están `@Disabled` con justificación)
