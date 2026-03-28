---
trigger: always_on
---

---
description: Normativas estrictas sobre manejo de PII y seguridad web
---

# Seguridad y Data Privacy (GDPR)

1. **Gestión de PII (Personally Identifiable Information):**
   - Nunca envíes datos personales (DNI, Nombres completos, Teléfonos, Correos, Direcciones exactas) a APIs de IA externas (Ej. Claude o ChatGPT) en los prompts o análisis. Aplica regex para enmascarar o generar *hashes*.
   - Los archivos subidos (Nóminas, Contratos) deben ir directamente a buckets S3 cifrados (AWS KMS) sin procesarse como Base64 en el cuerpo principal del backend a menos que sea estrictamente necesario. Usar *pre-signed URLs*.
2. **Autenticación y Autorización JWT:**
   - Todos los endpoints (`/api/v1/**`) están cerrados (Autenticados) por defecto. Se debe permitir acceso explícitamente vía Spring Security (Ej. endpoints públicos de listado).
   - El decorador/anotación `@PreAuthorize` o `@Secured` es obligatorio en métodos que modifiquen el estado (Crear, Actualizar, Borrar).
3. **Seguridad Web Frontend:**
   - Evitar `innerHTML` y si se usa, pasarlo forzosamente por el `DomSanitizer` de Angular para evitar XSS.
   - Manejo de Tokens JWT preferiblemente in-memory o HttpOnly cookies, no `localStorage` directo si se puede evitar.
