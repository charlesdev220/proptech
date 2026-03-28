---
description: Directrices para consultas e interacciones óptimas con PostgreSQL + y evitar N+1 queries.
---

# Reglas de Rendimiento de JPA/Hibernate

1. **Filtrado del N+1 Problem:**
   - Nunca confíes en el `FetchType.EAGER` masivo. Estandarizar relaciones en `FetchType.LAZY`.
   - Al buscar colecciones anidadas desde un servicio (ej: Inmueble y Fotos), debe usarse obligatoriamente la anotación de `@EntityGraph` en la firma del repositorio (o un JOIN FETCH) para resolverlo en una única Query.
2. **Paginación Absoluta:**
   - Ningún endpoint que devuelva colecciones de objetos grandes (`List<User>`, `List<Property>`) está permitido en producción sin paginar.
   - Obligación de usar `Pageable` inyectado en Controller y pasar a `Page<?>` de respuesta.
3. **Transaccionalidad:**
   - Métodos de solo lectura en Service deben estar anotados rigurosamente con `@Transactional(readOnly = true)` para evitar Flush de Hibernate innecesario y usar réplicas de sólo lectura.
