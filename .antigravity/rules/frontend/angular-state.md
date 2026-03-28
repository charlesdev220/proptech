---
description: Políticas sobre reactividad y gestión de estado local y global en Angular.
---

# Gestión de Estado Angular (Signals)

1. **Estado Local:** 
   - Está prohibido el uso de decoradores clásicos para inicialización mutable si se puede usar reactividad.
   - Uso obligatorio de `signal()`, `computed()` y `effect()` para datos reactivos internos de los componentes.
2. **Inputs y Outputs:** 
   - Utilizar las nuevas APIs estandarizadas: `input()`, `input.required()` y el uso de Signals para leerlos.
3. **Cuándo usar RxJS:**
   - La librería RxJS debe limitarse al consumo de APIs REST (`HttpClient`) y el uso de `BehaviorSubject`/`Observable` se reserva exclusivamente para flujos asíncronos complejos que necesiten manipulación de tiempo (`debounceTime`, `switchMap`, invocaciones basadas en EventListeners continuos).
   - Siempre intentar convertir el resultado de una llamada asíncrona rápidamente a Signal para su pintado en vista (`toSignal()`).
4. **Estado Global (NgRx):** 
   - Sólo usar store (Redux-pattern) para datos altamente compartidos entre diferentes *features* que no tengan una relación directa padre-hijo (ej. Token y Rol de usuario autenticado, Filtros globales de búsqueda persistentes).
