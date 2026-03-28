---
trigger: always_on
---
---
description: Reglas de arquitectura de carpetas y estructuración de componentes en Angular 17+
---

# Arquitectura Frontend Angular

1. **Standalone Components Obligatorios:** Todo componente, directiva o pipe nuevo debe usar `standalone: true`. Nunca usar ni crear `NgModule`.
2. **Estructura por Características (Feature-Driven):**
   - `/core`: Servicios *Singleton* de uso general (Interceptors, AuthService, Logging). Solo se inyectan en `app.config.ts`.
   - `/shared`: Componentes puramente visuales (Botones, Inputs, Modales), Pipes y Directivas que se reutilizan en múltiples features.
   - `/features`: Agrupación por lógica de negocio (ej: `/property-list`, `/user-dashboard`). Cada feature debe estar fuertemente encapsulada y no depender de otras si es posible.
3. **Smart vs Dumb Components:** 
   - Feature components son *Smart* (inyectan servicios y manejan estado global). 
   - Shared components son *Dumb* (solo reciben de `@Input()` y envían por `@Output()`).
4. **Control Flow:** Uso exclusivo de las nuevas directivas `@if`, `@for` integradas. Nunca usar `*ngIf` ni `*ngFor`.
