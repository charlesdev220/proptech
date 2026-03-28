# 🏙️ Plataforma PropTech de Nueva Generación

Bienvenido al repositorio principal de la **Plataforma PropTech**, un ecosistema integral que revoluciona el mercado inmobiliario (alquiler y venta) al combinar un potente motor de anuncios clasificados con una red social bidireccional basada en la **reputación y confianza verificada**.

---

## 💡 El Problema (¿Qué solucionamos?)
Los mercados inmobiliarios digitales actuales sufren de un problema fundamental: **la falta de confianza sistemática**. 
- Las grandes plataformas (Idealista, Fotocasa) operan puramente como directorios de anuncios vacíos de contexto real sobre las partes involucradas, propiciando el fraude y la fricción.
- Las herramientas de validación de inquilinos (RentSpree) son B2B y carecen de un marketplace público de cara al usuario.

**Nuestra Solución:** Cubrimos este espacio inexplorado creando un "LinkedIn para el sector inmobiliario". Un marketplace público enriquecido con validaciones biométricas (KYC), scoring financiero automatizado, un sistema de reseñas post-contrato entre propietarios e inquilinos, asistido por Inteligencia Artificial y Big Data de mercado.

---

## ⚙️ ¿Cómo Funciona?

### Para Buscadores (Inquilinos/Compradores)
1. Pueden explorar propiedades mediante un motor de búsqueda geoespacial impulsado por IA y mapas interactivos (Mapbox).
2. Construyen proactivamente su **Perfil de Confianza**, validando su identidad y subiendo (de forma encriptada) documentación de solvencia.
3. Se presentan a los propietarios no como extraños, sino como candidatos verificados con un "Trust Score" visible.

### Para Propietarios y Agencias
1. Publican inmuebles apoyándose en un Asistente IA que les ayuda a tasar el precio de mercado y generar descripciones atractivas.
2. Poseen un CRM propio donde pueden filtrar las solicitudes recibidas por grado de solvencia y puntajes previos de inquilinos.

### Reputación Bidireccional
Al finalizar un contrato de alquiler o compraventa a través de la plataforma, ambas partes se evalúan mutuamente (puntualidad, trato, estado del inmueble). Estas reseñas, inmutables y confirmadas, construyen el historial de cada usuario en el ecosistema.

---

## 🛠️ Stack Tecnológico (Arquitectura)

La aplicación sigue una arquitectura hexagonal en el backend y un diseño altamente modular orientado a *Standalone Components* en el frontend, preparado para escalar geográficamente.

### Frontend Web (SPA & SSR)
- **Framework:** Angular (Última versión) / Angular Universal (Para SEO).
- **Estilos:** Tailwind CSS + Angular CDK.
- **Estado Reactivo:** Signals & NgRx.
- **Mapas:** Mapbox GL JS.

### Backend API (Microservicios / Monolito Modular)
- **Entorno:** Java 21 LTS + Spring Boot 3.x.
- **Base de Datos:** PostgreSQL + extensión PostGIS (ManejoGeoespacial).
- **ORM / Mapeos:** Hibernate (JPA) + MapStruct para DTOs.
- **Seguridad:** Spring Security con JWT Stateless (Cifrado KMS).

### Infraestructura Cloud & DevOps
- **Cloud Provider:** Amazon Web Services (AWS).
- **Orquestación:** Docker + Kubernetes (Amazon EKS).
- **CI/CD:** Pipelines automatizadas con GitHub Actions.
- **Base de datos gestionada:** Amazon RDS Multi-AZ.

### Inteligencia Artificial
- **Interacciones Conversacionales / Generación:** Integraciones directas con **Anthropic Claude API** (Modelos Sonnet) para el asistente chatbot y generación de tasaciones (AVM).
- **Data Pipeline:** Procesos ETL para crear Heatmaps interactivos de mercado y oferta/demanda.

---

## 🏗️ Estructura del Proyecto

El repositorio se divide en dos grandes monolitos orquestados en un formato mono-repo:

```bash
📦 PropTech-Platform
 ┣ 📂 backend          # API Completa en Java / Spring Boot.
 ┣ 📂 frontend         # Aplicación Web en Angular 17+ (Core, Shared, Features).
 ┣ 📂 infra            # Scripts de despliegue IaC (Terraform, Docker Compose).
 ┣ 📂 .antigravity     # Ecosistema de Agentes IAC y reglas de desarrollo automáticas (LLMs).
 ┣ 📜 README.md
 ┗ 📜 docker-compose.yml # Entorno de levantamiento local (DB, Redis, etc.)
```

---

## 🚀 Entorno de Desarrollo Local (Getting Started)

### Prerrequisitos
- [Docker & Docker Compose](https://www.docker.com/) instalados.
- [Java 21 JDK](https://adoptium.net/) y Maven para el Backend.
- [Node.js 20+](https://nodejs.org/) y Angular CLI para el Frontend.

### Pasos Iniciales

1. **Clonar el repositorio y preparar la DB y servicios externos:**
   ```bash
   git clone <url-del-repositorio>
   cd proptech
   docker-compose up -d  # Levanta PostgreSQL + PostGIS, Redis y LocalStack
   ```

2. **Arrancar el Backend (Spring Boot):**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   # La API estará disponible en http://localhost:8080/api/v1
   # Y el contrato Swagger en http://localhost:8080/v3/api-docs
   ```

3. **Arrancar el Frontend (Angular):**
   ```bash
   cd frontend
   npm install
   npm run start
   # La aplicación Web estará disponible en http://localhost:4200
   ```

---

## 🤖 Ecosistema de Automatización IA (`.antigravity`)

Este proyecto es desarrollado de manera autónoma y controlada a través de un set de **Agentes de Inteligencia Artificial locales (.antigravity/)**. 
Dicho ecosistema incluye perfiles de Tech Leads, Arquitectos Angular, Especialistas Java, Testers QA y Expertos Devops. Se encargan de validar PRs (Pull Requests), aplicar migraciones automatizadas a las bases de datos (Liquibase) y autogenerar todo el esqueleto REST y tests de código.

---

> Propietario de Proyecto: [Tu Nombre o Empresa]
> Licencia: Software Privado y Confidencial.
