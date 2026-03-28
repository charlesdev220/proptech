---
description: Flujo de extremo a extremo para la implementación de una nueva funcionalidad técnica.
---

# Flujo: Implementación Feature Fullstack

**Rol objetivo:** Spring Architect + Angular Architect

Cuando se te asigne crear un módulo completo desde la base de datos hasta la interfaz visual, sigue **obligatoriamente** estos pasos en orden:

1. **Diseño de Modelo (Backend):** Crea o actualiza la entidad `@Entity` en JPA. No arranques la DB hasta generar el Changelog.
2. **Flujo C-R-U-D Básico:** Escribe el `Repository`, el `Service` y la interfaz del `Controller`.
3. **Contrato de API (Transversal):** Lanza la aplicación y extrae el `/v3/api-docs` para validar que el contrato OpenAPI concuerda.
4. **Validaciones SDET (Backend):** Escribe tests de JUnit/Mockito enfocados en lógica de negocio (Services).
5. **Generador de Cliente (Frontend):** Ejecuta la pipeline o script de OpenAPI Client generator para crear el `PropertyService` o `UserService` en UI.
6. **Implementación UI (Frontend):** Crea el componente Angular Standalone y enlázalo usando una Signal computada respecto al servicio autogenerado.
7. **Refinamiento UI+UX:** Asigna funcionalidad de validaciones (Reactive Forms) y directivas CSS limpias de Tailwind.
8. **Revisión End-to-End.**
