---
name: OpenAPI Client Generator
description: Integra el Swagger backend en Angular mediante scripts del openapi-generator-cli.
---

# Skill: Client Generation (REST API)

Cuando el orquestador pida sincronizar el frontend con el backend, debes seguir este proceso automatizado:

1. **Localizar el Contrato:** Extraé el archivo `/v3/api-docs` proporcionado por el archivo local `openapi.yaml` garantizado por el Backend Architect.
2. **Ejecución Local:** 
   ```bash
   npx @openapitools/openapi-generator-cli generate \
   -i openapi.yaml \
   -g typescript-angular \
   -o frontend/src/app/core/api \
   --additional-properties=ngVersion=17.0.0,providedInRoot=true
   ```
3. **Revisión del Módulo (Core):** Observa los servicios generados e inyéctalos con `inject()` en los componentes Angular necesarios. Prohíbe mutar los ficheros bajo la carpeta `core/api/` generada, a menos que sean interceptores HTTP separados.
