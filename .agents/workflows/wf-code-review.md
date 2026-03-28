---
description: Metodología para auditoría y revisión de código estática y de SQA.
---

# Flujo: Code Review Automático

**Rol objetivo:** Orchestrator + QA Tester

Antes de aprobar el paso de desarrollo a staging (Merge Request), evalúa estas condiciones:

1. **Pipeline Constraints:**
   - ¿Ha superado Angular su verificación `ng lint` / `eslint` sin *warnings*?
   - ¿Pasó el build de Maven/Gradle en Spring Boot?
   - ¿La cobertura por `Jacoco` supera el umbral del 80% en los módulos de negocio (Excluyendo DTOs)?
2. **Prácticas Arquitectónicas:**
   - ¿Se han usado RxJS Subjects fuera de llamadas asíncronas en Front? (Debe ser rechazado, forzar a *Signals*).
   - ¿El Backend incluye alguna inyección `@Autowired` de campo? (Debe ser rechazada hacia Constructor).
3. **Aislamiento Funcional:**
   - ¿Se está devolviendo el stacktrace nativo de Java en alguna Respuesta HTTP al frontend? Modificar obligatoriamente hacia un `@ControllerAdvice`.
4. **Conclusión de Ticket:** Comentar en PR (Simulado) los hallazgos y requerir cambios del especialista antes del merge.
