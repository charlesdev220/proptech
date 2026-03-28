---
name: generate-api-client
description: Integra el Swagger backend en Angular mediante scripts del openapi-generator-cli.
---

# OpenAPI Client Generator

Instrucciones para generar y sincronizar el cliente REST de Angular de forma automatizada a partir de la especificación OpenAPI del backend.

## When to use this skill

- Utiliza esta habilidad cuando el orquestador pida sincronizar el frontend con el backend después de cambios en la API.
- Esto es útil para mantener actualizados los modelos y servicios de Angular sin escribirlos manualmente, garantizando el desarrollo guiado por contratos (Contract-First).

## How to use it

1. **Localizar el Contrato:** Extrae el archivo `/v3/api-docs` proporcionado por el archivo local `openapi.yaml` garantizado por el Backend Architect.
2. **Ejecución Local:** Ejecuta el generador de OpenAPI para Angular.
   ```bash
   npx @openapitools/openapi-generator-cli generate \
   -i openapi.yaml \
   -g typescript-angular \
   -o frontend/src/app/core/api \
   --additional-properties=ngVersion=17.0.0,providedInRoot=true
   ```
3. **Revisión del Módulo (Core):** Observa los servicios generados e inyéctalos usando la función `inject()` en los componentes Angular necesarios.
4. **Restricción de Mutación:** Está estrictamente prohibido modificar los ficheros generados bajo la carpeta `frontend/src/app/core/api/`, a menos que sean interceptores HTTP separados creados manualmente fuera de la carpeta autogenerada.
