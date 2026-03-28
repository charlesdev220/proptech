---
name: Angular Deferrable Optimizer
description: Observador de métricas y promotor de Carga Diferida (Lazy Loading) moderna.
---

# Skill: Angular @defer Block Optimization

El experto Frontend y Angular Architect utilizará esta habilidad para optimizar el bundle inicial y el Time-to-Interactive (TTI) de las páginas públicas:

1. **Análisis del Bundle:** Verifica el tamaño de imports del componente. Usa `ng build --stats-json` -> source-map-explorer. Si el bundle es grande o involucra algo como una librería visual muy pesada (Echarts, MapBox, Three.js).
2. **Aplicación del Comando:** Envolver esa sección en la UI mediante un control flow `@defer`.
   Ejemplo:
   ```html
   @defer (on viewport) {
       <app-heavy-mapbox-viewer [listing]="listing()" />
   } @loading(minimum 1s) {
       <div class="skeleton-map"></div>
   }
   ```
3. El agente de UI está vetado de cargar componentes secundarios como mapas, modales de contacto complejos y librerías externas en el LCP (Largest Contentful Paint) principal. Deben delegarse.
