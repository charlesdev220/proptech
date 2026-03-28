---
name: Spring Boot Architecture
description: Reglas de Arquitectura Hexagonal y Clean Code para Java 21 / Spring Boot 3
---

# Estilos de Implementación: Spring Boot Architecture

## 1. Patrón de Capas Estricto (Ports & Adapters)
- **Capa Controller (API):** Encargada exclusivamente de orquestar HTTP Request/Responses (`@RestController`). No debe contener lógica de negocio. Solo conoce a la capa de Servicio.
- **Capa Service (Domain):** El núcleo de la lógica (`@Service`). No conoce que la data viene de la web o de una base de datos específica (sus interfaces la protegen).
- **Capa Repository (Infra):** Implementación de base de datos (`@Repository`).

## 2. Inmutabilidad y Transferencia de Datos
- Prohibición de uso `@Autowired` en atributos inyectados. Obligatorio el uso de **Constructor Injection** (Preferible vía `@RequiredArgsConstructor` de Lombok).
- Uso forzoso de **Java Records** (Java 21+) para todos los objetos DTOs que entren o salgan del Controller. Las `@Entity` no deben filtrarse.
- Conversión DTO <-> Entity a través de **MapStruct**.

## 3. Manejo de Estado (Stateless)
- Toda invocación web debe ser manejada de forma "Stateless" confiando en JWT. Evitar dependencias en sesión del servidor.
