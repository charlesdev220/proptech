---
name: API Test Generator
description: Auto-genera tests TDD/BDD de integración consumiendo directamente el contrato OpenAPI o las especificaciones del usuario.
---

# Skill: API Test Generator

## Descripción
Esta habilidad está diseñada para el agente de QA (`qa-automation`). En lugar de escribir aserciones una por una, este script u ordenanza permite leer el `openapi.yaml` actual y proponer/implantar una batería completa de tests.

## Instrucciones de Uso (Para el Agente)
1. **Rastrear Endpoint:** Localiza el Swagger/OpenAPI (`openapi.yaml`) y extrae los Códigos de Status (200, 400, 401).
2. **Generar Plantilla Spring Boot (Integration Test):**
   Crea una prueba de integración con `@SpringBootTest` y `Mock` que valide todos los flujos de respuesta HTTP y compruebe los errores RFC 7807 para el backend.
3. **Generar Plantilla Cypress (Mock E2E):**
   Crea scripts automatizados de tipo `cy.intercept('POST', '/api/v1/...')` para validar que el interceptor global front reaccione acorde.
4. **Validar:** Notifica al sistema cuántos Edge Cases han sido cubiertos.
