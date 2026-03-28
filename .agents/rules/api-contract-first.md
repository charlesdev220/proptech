---
trigger: always_on
---
---
description: Reglas para la obligatoriedad del diseño de API Contract-First
---

# Desarrollo Guiado por Contratos (Contract-First)

1. **Diseño Previo Obligatorio:**
   - Ninguna línea de código de un Controlador de Spring o un Servicio de Angular se escribirá sin antes definir o modificar la especificación `openapi.yaml` (o `.json`).
   - El archivo Swagger/OpenAPI es la fuente única de la verdad.
2. **Generación Automática:**
   - Los modelos de Typescript (API Client) deben generarse siempre a través de OpenAPI Generator basándose en el contrato del backend. Prohibido escribir interfaces a mano que representen Respuestas HTTP.
3. **Manejo Estructurado de Errores (@ControllerAdvice):**
   - El contrato debe incluir especificaciones de errores `400 Bad Request`, `401 Unauthorized`, `404 Not Found` bajo un formato estándar común en toda la aplicación (ej. `RFC 7807`), para que el frontend pueda manejarlo genéricamente a través de Angular Interceptors.
