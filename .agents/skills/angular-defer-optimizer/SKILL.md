---
name: angular-defer-optimizer
description: Observador de métricas y promotor de Carga Diferida (Lazy Loading) moderna. Usa esta habilidad para optimizar el bundle inicial y el Time-to-Interactive (TTI) de las páginas públicas.
---

# Angular Deferrable Optimizer

Instrucciones para optimizar el bundle inicial de Angular y el Time-to-Interactive (TTI) utilizando el bloque `@defer`.

## When to use this skill

- Utiliza esta habilidad cuando el bundle de la aplicación sea muy grande y requiera optimización.
- Esto es útil para cargar de forma diferida (lazy loading) componentes pesados como mapas (MapBox), gráficos (Echarts), modelos 3D (Three.js) o modales complejos que no necesitan estar presentes en el LCP (Largest Contentful Paint) principal.

## How to use it

1. **Análisis del Bundle:** Verifica el tamaño de los imports del componente. Utiliza herramientas como `ng build --stats-json` y `source-map-explorer` para identificar librerías visuales muy pesadas.
2. **Aplicación del Comando:** Envuelve la sección pesada de la UI utilizando la sintaxis de control de flujo `@defer`.
   Ejemplo:
   ```html
   @defer (on viewport) {
       <app-heavy-mapbox-viewer [listing]="listing()" />
   } @loading(minimum 1s) {
       <div class="skeleton-map"></div>
   }
   ```
3. **Restricción de Carga:** El agente de UI tiene prohibido cargar componentes secundarios y librerías externas pesadas en el renderizado inicial (LCP). Siempre deben delegarse usando bloques `@defer`.
