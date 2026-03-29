---
name: Spring Boot Core
description: Pautas principales sobre el ecosistema y dependencias de Spring Boot
---

# Estilos de Implementación: Spring Boot Core

## 1. Manejo Estructurado de Errores
- Utilización de un manejo de errores robusto tipo `@ControllerAdvice`.
- Toda excepción de negocio (Ej: `PropertyNotFoundException`) debe ser interceptada y devuelta bajo una estructura estandarizada, idealmente el formato RFC 7807 (Problem Details for HTTP APIs - `ProblemDetail.forStatusAndDetail(...)`).

## 2. Contract-First (OpenAPI)
- Ningún código principal debe ser implementado sin antes definir las rutas en un contrato OpenAPI local o YAML.
- Provee de descripciones rigurosas y códigos de error permitidos por cada endpoint. Mantenlo sincronizado permanentemente.
