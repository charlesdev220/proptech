---
name: Dockerize App
description: Automatiza la generación de Dockerfiles multi-stage de altísimo rendimiento para backend (Spring Boot) y frontend (Angular/Nginx), junto al Compose orchestrator.
---

# Skill: Dockerize App

## Descripción
Esta habilidad está diseñada para el agente DevOps (`devops-cloud`). Asegura la persistencia local de la integración y el provisionamiento de los entornos estandarizados.

## Instrucciones de Uso (Para el Agente)
Cuando se te ordene dockerizar el proyecto:
1. **Spring Boot (Backend):**
   Produce un `Dockerfile` multi-etapa usando Eclipse Temurin o Amazon Corretto. Añade la capa de dependencias (para utilizar caché en build success) y separa la generación del `.jar`.
2. **Angular (Frontend):**
   Produce un `Dockerfile` usando Node (pnpm/npm) para el build principal y una imagen súper liviana de `nginx:alpine` para servir el output dist copiando un `nginx.conf` optimizado (con fallbacks a `index.html`).
3. **Servicios (Docker Compose):**
   Construye un `docker-compose.yml` en la raíz enlazando ambos contenedores junto a los servicios vitales (PostgreSQL + PostGIS, RabbitMQ, etc.). Inserta `healthchecks` adecuados.
4. **Respuesta:** Enseña al Orchestrator que el entorno está *Ready-To-Run*.
