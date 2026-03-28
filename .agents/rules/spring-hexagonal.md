---
trigger: always_on
---

---
description: Aislamiento de capas en Java/Spring Boot basado en arquitectura Hexagonal.
---

# Arquitectura Backend Spring Boot (Hexagonal)

1. **Patrón de Capas Estricto:**
   - `Layer API (Controller)`: Exponer REST, procesa Request/Response HTTP en formato DTO. Conoce a `App Layer`.
   - `Layer Domain (Service)`: Lógica pura de negocio. Interfaz central. Conoce de interfaces, pero NO implementaciones de base de datos.
   - `Layer Infra (Repository)`: Adaptadores de base de datos (`JpaRepository`, implementaciones).
2. **Política de Transferencia de Objetos (MDTOs):**
   - Una entidad anotada con `@Entity` o `@Table` (Hibernate) **nunca** debe alcanzar el controlador REST o ser la firma de retorno de una API.
   - Todo debe ser transferido usando `Records` (DTO) en Java 21+. 
   - Uso obligatorio de **MapStruct** para convertir eficientemente entre Entity -> DTO y viceversa en la capa de Servicio.
3. **Inyección de Dependencias:** Usar Constructor Injection (Preferiblemente con Lombok `@RequiredArgsConstructor`). Prohibido `@Autowired` en variables de campo (Field Injection).
