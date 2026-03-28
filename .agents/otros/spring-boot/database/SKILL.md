---
name: Spring Boot Database & JPA
description: Optimizaciones y reglas de concurrencia y acceso DB con PostGIS y Hibernate.
---

# Estilos de Implementación: Spring Boot Database

## 1. Concurrencia y Optimizaciones N+1
- Es imperativo el uso de `@EntityGraph` (o `JOIN FETCH` directo en JPQL) para las colecciones relacionadas cuando son solicitadas por el servicio (Ej: Propiedad `->` Fotos). Evita el clásico problema N+1.
- Estandarización de `FetchType.LAZY` en relaciones `OneToMany` y `ManyToMany`. Carga Eager deshabilitada por defecto.

## 2. Transaccionalidad Consciente
- Etiquetar estrictamente los métodos de solo lectura en Service con `@Transactional(readOnly = true)` para evitar que el EntityManager persiga flushes innecesarios, lo cual mejora el rendimiento general.

## 3. Geoespacial
- Si se manejan coordenadas o polígonos, se deben castear correctamente a objetos y tipos de `org.locationtech.jts.geom` respaldados interactivamente por PostGIS para cálculos rápidos de distancia / geofencing en BD.
