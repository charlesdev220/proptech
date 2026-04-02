## Exploration: go-live-localizado

### Current State

El sistema PropTech tiene un núcleo funcional sólido (Auth JWT, Búsqueda Geoespacial, Perfiles, Trust Score, KYC Upload, Mapa Leaflet). Sin embargo, presenta brechas críticas de seguridad, testing insuficiente (<10% coverage), y ausencia total de infraestructura de stress testing y onboarding beta.

### Affected Areas

**Backend Controllers:**
- `backend/src/main/java/com/proptech/backend/api/controller/AuthController.java` — endpoints login/register
- `backend/src/main/java/com/proptech/backend/api/controller/PropertyController.java` — CRÍTICO: POST permitAll sin auth
- `backend/src/main/java/com/proptech/backend/api/controller/ProfileController.java` — GET profile/trust-score; POST /documents NO IMPLEMENTADO
- `backend/src/main/java/com/proptech/backend/api/controller/MediaController.java` — upload/download medios

**Backend Config:**
- `backend/src/main/java/com/proptech/backend/infrastructure/config/SecurityConfig.java` — reglas de autorización incorrectas
- `backend/src/main/resources/application.properties` — jwt.secret hardcodeado, show-sql=true

**Tests:**
- `backend/src/test/java/com/proptech/backend/BackendApplicationTests.java` — solo contextLoads
- `backend/src/test/java/com/proptech/backend/api/controller/PropertyControllerTest.java` — incompleto
- `frontend/src/app/app.component.spec.ts` — básico
- `frontend/scripts/e2e-smoke.sh` — smoke test básico existente

**CI/CD:**
- `.github/workflows/ci.yml` — no ejecuta tests, sin Docker, sin staging deploy

**Seed Data:**
- `backend/src/main/java/com/proptech/backend/infrastructure/persistence/seeder/DataSeeder.java` — solo 1 user + 3 propiedades (insuficiente para beta)

### Approaches

1. **Go-Live Minimalista (Beta Cerrado)**
   - Corregir brechas de seguridad bloqueantes, escribir tests críticos (smoke), stress test básico con k6, ampliar seed data para onboarding de los primeros 100 usuarios.
   - Pros: entregable en ~1 semana, foco en lo que bloquea el beta real
   - Cons: coverage no llega al 70%, no hay CD automatizado
   - Effort: **Medium**

2. **Go-Live Completo (Producción)**
   - Todo lo anterior + Jacoco 70% coverage, SonarQube, Docker, Kubernetes, monitoring Prometheus/Grafana, rate limiting, feature flags.
   - Pros: base sólida para escalar
   - Cons: semanas adicionales, overkill para 100 usuarios beta
   - Effort: **High**

3. **Go-Live por Capas (Priorizado)**
   - Sprint 1: seguridad + tests críticos + smoke. Sprint 2: stress testing. Sprint 3: seed onboarding + CI mejorado.
   - Pros: iterativo, cada sprint entrega valor medible
   - Cons: requiere coordinación entre sprints
   - Effort: **Medium**

### Recommendation

**Enfoque 3 — Go-Live por Capas**, priorizando los bloqueantes de seguridad y luego stress testing. Para 100 usuarios beta localizado no se necesita Kubernetes ni 70% coverage; se necesita que el sistema sea seguro, no explote bajo carga básica, y que los primeros usuarios tengan datos realistas para explorar.

**Prioridad de issues identificados:**

| # | Bloqueante | Archivo | Tipo |
|---|-----------|---------|------|
| 1 | POST /properties sin auth | SecurityConfig.java | Seguridad CRÍTICA |
| 2 | /profile/documents no implementado | ProfileController.java | Feature incompleta |
| 3 | Sin @ControllerAdvice | — | Estabilidad |
| 4 | Stress testing ausente | — | Performance |
| 5 | Seed insuficiente (1 user, 3 props) | DataSeeder.java | Onboarding beta |
| 6 | CI no corre tests | ci.yml | Calidad |

### Risks

- **Seguridad:** POST /properties completamente abierta — cualquier anónimo puede crear listados falsos
- **Estabilidad:** sin @ControllerAdvice, excepciones exponen stacktraces Java al cliente
- **Performance:** sin datos sobre capacidad del sistema bajo carga (geospatial queries no están indexadas explícitamente)
- **Onboarding:** con 1 usuario y 3 propiedades en Madrid los primeros beta testers no tienen con qué interactuar
- **Regresiones:** CI sin tests — cualquier push podría romper la app en silencio

### Ready for Proposal

**Sí** — hay suficiente claridad para proponer. Los issues son concretos, los archivos están identificados, y el scope del beta (100 usuarios localizados) define un nivel de ambición alcanzable.
