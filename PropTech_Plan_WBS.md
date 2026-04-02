**PROPTECH PLATFORM**

Alquiler & Venta con Red Social de Reputación

+-----------------------------------------------------------------------+
| **PLAN DE TRABAJO & WBS EXHAUSTIVO**                                  |
|                                                                       |
| Product Manager + Arquitecto de Software Senior                       |
+-----------------------------------------------------------------------+

Versión 1.0 • Marzo 2026

**Tabla de Contenidos**

**1. Resumen Ejecutivo**

Este documento constituye el Plan de Trabajo completo y la Work Breakdown Structure (WBS) para el desarrollo de una plataforma PropTech de nueva generación orientada al mercado español e iberoamericano. La plataforma combina un motor de clasificados inmobiliarios con dinámicas propias de red social y un sistema de reputación bidireccional, creando un ecosistema de confianza que aborda directamente las principales fricciones del mercado de alquiler y venta actual.

+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **🎯 Propuesta de Valor Diferencial**                                                                                                                                                                                                                                                                                                                           |
|                                                                                                                                                                                                                                                                                                                                                                 |
| Mientras Idealista y Fotocasa son directorios de anuncios (sin capa de confianza), y plataformas como RentSpree o TurboTenant son herramientas de gestión interna (sin marketplace público), esta plataforma ocupa el espacio inexplorado: un marketplace público con capa de confianza verificada, scoring de solvencia, IA integrada y Big Data inmobiliario. |
+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

El proyecto se estructura en 4 fases evolutivas que van desde un MVP funcional (6 meses) hasta una plataforma completa con IA y Big Data (18 meses). La arquitectura propuesta es modular, escalable y diseñada para cumplir con la normativa GDPR desde el día uno.

**2. Benchmarking: Plataformas de Referencia**

Antes de diseñar la arquitectura, analizamos tres categorías de plataformas que resuelven partes del problema y de las que podemos extraer lecciones críticas.

**2.1 Capa de Clasificados: Idealista & Fotocasa (España)**

Idealista es el líder indiscutible del mercado español con la mayor base de datos de inmuebles y una estrategia SEO superior. Fotocasa, propiedad de Adevinta, destaca por sus herramientas innovadoras de búsqueda como el mapa de calor de precios y la búsqueda dibujando en el mapa.

  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  **Plataforma**      **Fortalezas**                                                                   **Debilidades Clave (Gap)**
  ------------------- -------------------------------------------------------------------------------- -------------------------------------------------------------------------------
  Idealista           SEO dominante, mayor inventario, presencia multinacional (ES/PT/IT)              Sin capa de confianza, fraude frecuente, reputación inexistente para usuarios

  Fotocasa            Heat map de precios, búsqueda por área dibujada, enfoque rental                  Público principalmente español, sin scoring de inquilinos, sin IA

  Loción aprendida    Inventario y SEO son la base; la innovación en UX de mapa es un differentiator   El mercado padece desconfianza sistematizada --- es el gap que explotamos
  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**2.2 Capa de Confianza y Scoring: RentSpree & Naborly**

RentSpree ofrece un flujo completo de solicitud y screening (background checks, ingresos, crédito) para el mercado norteamericano. Naborly aplica IA y analítica avanzada para scoring de riesgo de inquilinos, yendo más allá del crédito tradicional.

  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  **Plataforma**      **Qué hacen bien**                                                                          **Cómo lo superamos**
  ------------------- ------------------------------------------------------------------------------------------- ----------------------------------------------------------------------
  RentSpree           Automatización del proceso de solicitud, verificación de ingresos, comunicación integrada   Solo es B2B (para property managers), no es un marketplace público

  Naborly             AI risk scoring, análisis más allá del crédito, decisioning automático                      Sin marketplace, sin red social, enfoque HOA/Condos

  Leción              El scoring multidimensional (no solo crédito) es el futuro del screening                    Embeber el scoring en un marketplace público crea un moat defensible
  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**2.3 Capa de IA y Asistencia: Zillow + EliseAI**

Zillow integra Zestimate (AVM) para estimación automática de valor, tours 3D y búsqueda natural. EliseAI automatiza leasing con IA conversacional 24/7 vía SMS, email y web chat, respondiendo consultas, agendando visitas y haciendo seguimiento.

+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **💡 Insight Clave del Benchmarking**                                                                                                                                                                                                                                              |
|                                                                                                                                                                                                                                                                                    |
| Ningún actor combina hoy los tres elementos: (1) Marketplace público con inventario masivo + (2) Sistema de confianza y reputación bidireccional verificado + (3) IA asistente integrada para ambos lados de la transacción. Esa combinación es la tesis central de este producto. |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

**3. Estructura de la Plataforma: Sitemap y Módulos**

La plataforma se estructura en 8 módulos principales que interoperan a través de una API central. Cada módulo puede desplegarse y escalarse de forma independiente.

**3.1 Mapa de Módulos (Vista General)**

  ---------------------------------------------------------------------------------------------------------------------------------------------------------
  **\#**   **Módulo**                      **Sub-componentes principales**                                            **Usuarios objetivo**
  -------- ------------------------------- -------------------------------------------------------------------------- -------------------------------------
  M1       Público / Marketing             Home, SEO landing pages, Blog, Precios públicos                            Visitantes anónimos

  M2       Motor de Búsqueda               Filtros avanzados, Mapa interactivo, Alertas, IA de búsqueda               Buscadores (compradores/inquilinos)

  M3       Ficha de Inmueble               Galeria multimedia, Video, Tour virtual, Info zona, Historial precios      Todos los usuarios

  M4       Panel Propietario/Agente        Gestionar anuncios, Estadísticas, CRM leads, IA tasación                   Propietarios y agencias

  M5       Perfil de Confianza (Scoring)   Subida documentos, Score solvencia, Historial de resenas, Badges           Propietarios e inquilinos

  M6       Sistema de Resenas              Resenas verificadas post-contrato, Valoraciones numericas, Moderación IA   Propietarios e inquilinos

  M7       Módulo IA                       Asistente búsqueda, Tasador IA, Redactor anuncios, Chatbot                 Todos los usuarios

  M8       Big Data / Mercado              Heatmaps precios, Oferta/demanda en tiempo real, Indices por zona          Público y anunciantes

  M9       Administración (BackOffice)     Moderación, KYC, Fraude, Billing, Soporte                                  Equipo interno
  ---------------------------------------------------------------------------------------------------------------------------------------------------------

**3.2 Módulo 1: Público y SEO**

-   Página de inicio con búscara prominente y prop value statement

-   Landing pages genéricas: /alquiler/madrid, /compra/barcelona (SEO programmatic)

-   Blog inmobiliario (contenido, guías, tendencias de mercado)

-   Página de precios y modelo de negocio público

-   Footer con sitemap y enlaces legales (GDPR, cookies, privacidad)

**3.3 Módulo 2: Motor de Búsqueda**

-   Filtros avanzados: tipo de inmueble, precio, superficie, habitaciones, certificado energético, mascosas, etc.

-   Mapa interactivo con dirección exacta (diferenciador clave vs competencia)

-   Búsqueda por radio, zona postal o municipio

-   Búsqueda por IA: lenguaje natural (\'piso luminoso cerca del metro con terraza bajo 1200€\')

-   Sistema de alertas por email/push para nuevos inmuebles que encajen con perfil

-   Guardado de favoritos y compartir búsqueda

-   Heatmap de precios por zona integrado en el mapa

**3.4 Módulo 3: Ficha de Inmueble**

-   Galeria de fotos de alta resolución con lightbox

-   Reproductor de video embebido y/o tour virtual 360º

-   Descripción completa (asistida o generada por IA)

-   Mapa con ubicación exacta y puntos de interés cercanos (transporte, colegios, supermercados)

-   Historial de precios del inmueble y evolución del mercado en esa zona

-   Perfil de confianza del propietario/anunciante (score + reseñas visibles)

-   Botón de contacto / solicitud de visita con formulario filtrado por score del buscador

-   Estimación de gastos mensuales (comunidad, IBI, seguro estimado)

**3.5 Módulo 4: Panel del Propietario / Agente (Dashboard)**

**Gestión de Anuncios**

-   Crear, editar, publicar y pausar anuncios

-   Subida masiva de fotos/vídeos con compresión en cliente (HTML5 Canvas) y almacenamiento BBDD

-   Asistente IA para generar título, descripción y keywords SEO del anuncio

-   Herramienta de tasación IA (precio de mercado sugerido por zona/tipología)

**CRM de Leads e Interesados**

-   Bandeja de solicitudes con perfil y score del interesado visible antes de responder

-   Historial de conversaciones con cada interesado

-   Gestión de visitas (agenda integrada con confirmación automática)

**Analytics de Anuncios**

-   Impressiones, clics, tasa de contacto, tiempo medio de visualización

-   Comparativa con anuncios similares en la zona

-   Perfil demográfico y de solvencia de los visitantes

-   Funnel de conversión: vista → contacto → visita → contrato

**3.6 Módulo 5: Perfil de Confianza (Scoring & Documentación)**

Este es el núcleo diferencial de la plataforma. Cada usuario construye un Perfil de Confianza compuesto por datos verificados.

  -----------------------------------------------------------------------------------------------------------------
  **Componente del Score**          **Fuente de datos**                                     **Peso aprox.**
  --------------------------------- ------------------------------------------------------- -----------------------
  Verificación de identidad (KYC)   DNI/Pasaporte + selfie biométrico                       15%

  Solvencia económica               Nóminas, declaración IRPF, contrato laboral             30%

  Historial de reseñas              Valoraciones de contratos previos                       30%

  Comportamiento en plataforma      Respuesta a mensajes, citas cumplidas                   15%

  Antigüedad y actividad            Tiempo en plataforma, nú de transacciones completadas   10%
  -----------------------------------------------------------------------------------------------------------------

-   El score se muestra como un número (0-100) y un nivel: Bronce / Plata / Oro / Platino

-   Los documentos financieros se almacenan cifrados y solo se comparten con consentimiento explcito (GDPR Art. 6)

-   El propietario puede filtrar solicitudes por score mínimo

-   El buscador puede mostrar su score a propietarios como carta de presentación proactiva

**3.7 Módulo 6: Sistema de Reseñas Bidireccional**

-   Activación de reseña post-contrato: ambas partes reciben invitación al finalizar el arrendamiento

-   Valoración numérica (1-5) + comentario escrito en hasta 500 caracteres

-   Dimensiones de valoración separadas: puntualidad de pagos, trato, estado del inmueble, etc.

-   Moderación automática por IA para detectar contenido ofensivo o falso

-   Mecanismo de respuesta: la parte valorada puede responder públicamente

-   Las reseñas son inmutables una vez publicadas (14 días para rectificación)

-   Reseñas verificadas = solo post-contrato confirmado en plataforma. No se permiten reseñas anónimas

**3.8 Módulo 7: Asistencia IA**

**Para Propietarios y Agentes**

-   Tasador IA: precio de alquiler/venta recomendado basado en comparables de la zona

-   Generador de descripción: redacta el anuncio automáticamente desde fotos y datos básicos

-   Detector de preguntas frecuentes: responde automáticamente preguntas repetidas de buscadores

-   Alertas predictivas: \'tu inmueble lleva X días sin contactos, considera bajar el precio un 5%\'

**Para Buscadores**

-   Asistente de búsqueda por lenguaje natural con refinamiento iterativo

-   Comparador IA: muestra pros/contras de dos inmuebles favoritos

-   Calculadora de viabilidad: dados mis ingresos y este precio, ¿puedo permitirlo?

-   Resumen de zona: seguridad, transporte, colegios, tiempo de desplazamiento al trabajo

-   Chatbot 24/7 para preguntas sobre el proceso de alquiler/compra

**3.9 Módulo 8: Big Data Inmobiliario**

-   Dashboard público de mercado: precio medio por m² por municipio y distrito

-   Gráficas de tendencia temporal: evolución de precios en los últimos 12 meses

-   Ratio oferta/demanda por zona (inmuebles disponibles vs consultas)

-   Tiempo medio de comercialización por tipología y zona

-   Heatmap de precios superpuesto al mapa de búsqueda

-   Informe de mercado descargable (PDF) --- funcionalidad premium para profesionales

-   API de datos abierta para medios de comunicación e investigadores (modelo freemium)

**4. WBS --- Work Breakdown Structure Completa**

La WBS se organiza en 9 áreas de trabajo y 4 fases temporales. Cada área se descompone hasta el nivel de tarea entregable.

**4.1 Área 1: Fundamentos de Arquitectura y DevOps**

**1.1 Infraestructura Cloud**

-   1.1.1 Selección y configuración de proveedor cloud (AWS o GCP recomendado)

-   1.1.2 Diseño de arquitectura multi-zona (alta disponibilidad)

-   1.1.3 Configuración de VPC, subnets públicas/privadas y security groups

-   1.1.4 Implementación de CDN para assets estáticos y media

-   1.1.5 Sistema de copias de seguridad automáticas y disaster recovery

**1.2 CI/CD y DevOps**

-   1.2.1 Configuración de repositorios Git con branching strategy

-   1.2.2 Pipeline CI/CD (GitHub Actions o GitLab CI)

-   1.2.3 Containerización con Docker y orquestación Kubernetes (EKS/GKE)

-   1.2.4 Entornos: Dev / Staging / Production con feature flags

-   1.2.5 Monitoreo y alertas (Datadog o New Relic)

**1.3 Seguridad y Cumplimiento**

-   1.3.1 Auditoría de seguridad y modelo de amenazas (threat modeling)

-   1.3.2 Implementación OWASP Top 10

-   1.3.3 Cifrado en tránsito (TLS 1.3) y en reposo (AES-256) para documentos sensibles

-   1.3.4 Política GDPR: consentimiento, derecho al olvido, portabilidad de datos

-   1.3.5 Penetration testing inicial y trimestral

**4.2 Área 2: Backend y API**

**2.1 Diseño del Modelo de Datos**

-   2.1.1 Entidades principales: User, Property, Listing, Review, Score, Document, Lead, Transaction

-   2.1.2 Diseño del esquema PostgreSQL con extensión PostGIS para datos geoespaciales

-   2.1.3 Estrategia de indexación para consultas de búsqueda

-   2.1.4 Migraciones con versionado (Flyway o Liquibase)

**2.2 API REST / GraphQL**

-   2.2.1 Diseño de contratos de API (OpenAPI 3.0 spec)

-   2.2.2 Autenticación JWT + refresh tokens con rotación automática

-   2.2.3 Rate limiting y protección contra abuso

-   2.2.4 Versionado de API (v1, v2) para compatibilidad futura

-   2.2.5 Documentación interactiva (Swagger UI)

**2.3 Servicios Core**

-   2.3.1 Servicio de Inmuebles: CRUD, filtros geoespaciales, búsqueda full-text (Elasticsearch)

-   2.3.2 Servicio de Usuarios y Autenticación (OAuth2 + social login)

-   2.3.3 Servicio de Scoring: cálculo y actualización del score compuesto

-   2.3.4 Servicio de Reseñas: flujo post-contrato, moderación, publicación

-   2.3.5 Servicio de Notificaciones: email (SES), push (FCM), SMS (Twilio)

-   2.3.6 Servicio de Mensajería interna entre propietario e interesado

-   2.3.7 Servicio de Pagos: subscripciones y anuncios destacados (Stripe)

**4.3 Área 3: Módulo de Documentación y KYC**

**3.1 Subida Segura de Documentos**

-   3.1.1 Flujo de carga con validación de tipo y tamaño de archivo
-   3.1.2 Almacenamiento cifrado en **PostgreSQL (Large Objects)**, reemplazando S3 para almacenamiento local de medios
-   3.1.3 Cuarentena antivirus (ClamAV o servicio cloud) antes de procesado
-   3.1.4 Permisos granulares: propietario del documento controla quién accede

**3.2 Verificación de Identidad (KYC)**

-   3.2.1 Integración con proveedor KYC (Onfido, Veriff o Jumio)

-   3.2.2 Verificación biométrica de documento + selfie

-   3.2.3 Verificación de número de teléfono (OTP)

-   3.2.4 Verificación de email

**3.3 Evaluación de Solvencia**

-   3.3.1 Parser automático de nóminas (OCR + extracción de datos clave)

-   3.3.2 Integración opcional con CIRBE o bureaus de crédito (Equifax ES)

-   3.3.3 Algoritmo de scoring financiero configurable por el equipo de riesgo

-   3.3.4 Panel de revisión manual para casos límite

**4.4 Área 4: Frontend Web**

**4.1 Arquitectura Frontend**

-   4.1.1 SPA con Angular (última versión) para SSR híbrido y arquitectura robusta

-   4.1.2 Design System propio basado en Tailwind CSS + Radix UI

-   4.1.3 Internacionalización (i18n): ES, EN, PT como idiomas iniciales

-   4.1.4 Accesibilidad WCAG 2.1 AA desde el inicio

**4.2 Páginas y Componentes Clave**

-   4.2.1 Home y buscador principal

-   4.2.2 Página de resultados con mapa interactivo (Mapbox GL o Google Maps)

-   4.2.3 Ficha de inmueble (multimedia, scoring propietario, boton de contacto)

-   4.2.4 Dashboard propietario/agente (análitica, CRM, gestión anuncios)

-   4.2.5 Perfil de usuario público (score, reseñas, documentación verificada)

-   4.2.6 Flujo de publicación de anuncio (wizard multi-paso con asistencia IA)

-   4.2.7 Página de Big Data público (heatmaps, tendencias, comparativas)

**4.3 Rendimiento y SEO**

-   4.3.1 Core Web Vitals objetivo: LCP \< 2.5s, FID \< 100ms, CLS \< 0.1

-   4.3.2 Generación estática de landing pages SEO programáticas

-   4.3.3 Schema.org markup para inmuebles (RealEstateListing)

-   4.3.4 Sitemap dinámico y robots.txt optimizado

**4.5 Área 5: Geolocaliz ación y Mapas**

-   5.1 Integración Mapbox GL JS o Google Maps Platform con tiles personalizados

-   5.2 Geocodificación de direcciones (Nominatim OSM + Mapbox Geocoding API)

-   5.3 Clustering inteligente de marcadores para densidades altas

-   5.4 Heatmap de precios generado con datos propios de la plataforma

-   5.5 Búsqueda por área dibujada en el mapa (Polygon search PostGIS)

-   5.6 Cálculo de isocronas: \'inmuebles a 20 min en metro de mi trabajo\'

-   5.7 Visualización de servicios cercanos (OpenStreetMap Overpass API)

**4.6 Área 6: Módulo de IA**

**6.1 Infraestructura IA**

-   6.1.1 Integración con API Claude (Anthropic) como LLM principal

-   6.1.2 Capa de abstracción para cambiar de modelo fácilmente

-   6.1.3 Sistema de prompts versionados y testeados

-   6.1.4 Caché de respuestas frecuentes para reducir costes

**6.2 Funcionalidades IA para Propietarios**

-   6.2.1 Generador de anuncios: recibe fotos + datos basicos y genera descripción

-   6.2.2 Tasador IA: AVM basado en comparables de la BD y datos de mercado

-   6.2.3 Respuesta automática a FAQs de interesados

**6.3 Funcionalidades IA para Buscadores**

-   6.3.1 Búsqueda por lenguaje natural con conversión a filtros estructurados

-   6.3.2 Chatbot asistente 24/7 integrado en la interfaz

-   6.3.3 Comparador inteligente de dos inmuebles favoritos

-   6.3.4 Calculadora de viabilidad financiera (ratio esfuerzo/ingresos)

**6.4 IA de Moderación y Fraude**

-   6.4.1 Detección de anuncios fraudulentos o duplicados

-   6.4.2 Moderación automática de reseñas y comentarios

-   6.4.3 Detección de documentos falsificados (anomalías en metadatos y contenido)

**4.7 Área 7: Big Data y Analytics**

-   7.1 Pipeline de datos: ingesta, transformación y carga (ETL con Apache Airflow)

-   7.2 Data Warehouse (BigQuery o Redshift) para análisis histórico

-   7.3 Cálculo de índices de precio por zona y tipología (actualización diaria)

-   7.4 Dashboard de inteligencia de mercado público (React + Recharts/D3)

-   7.5 Motor de reportes personalizado para anunciantes premium

-   7.6 Detección de anomalías de precio (alertas para el equipo de operaciones)

-   7.7 API de datos agregados públicos (rate limited) para medios e investigadores

**4.8 Área 8: Módulo de App Móvil**

-   8.1 App React Native (iOS + Android) con código compartido con web

-   8.2 Notificaciones push para alertas de nuevos inmuebles y mensajes

-   8.3 Escaneo de documentos desde cámara del móvil

-   8.4 Navegación hasta el inmueble desde la app

-   8.5 Modo offline básico para favoritos guardados

**4.9 Área 9: BackOffice y Operaciones**

-   9.1 Panel de administración (roles: superadmin, moderador, soporte, analista)

-   9.2 Moderación de anuncios: cola de revisión, herramientas de aprobación/rechazo

-   9.3 Gestión de usuarios: bloqueo, verificación manual, historial de acciones

-   9.4 Sistema de tickets de soporte integrado (Zendesk o Intercom)

-   9.5 Facturación y subscripciones: generación de facturas, refunds, dunning

-   9.6 Dashboard de KPIs del negocio: MAU, CAC, churn, listings activos, GMV

**5. Fases del Proyecto: De MVP a Lanzamiento**

  ------------------------------------------------------------------------------------------------------------------------------------------------------------
  **Fase**            **Periodo**    **Objetivo**                                **Entregables clave**
  ------------------- -------------- ------------------------------------------- -----------------------------------------------------------------------------
  Fase 1: MVP         Meses 1-6      Validar el mercado con funcionalidad core   Motor búsqueda, fichas, perfiles basicos, scoring inicial, mapa

  Fase 2: Confianza   Meses 7-10     Activar el diferenciador de reputación      Reseñas bidireccionales, KYC avanzado, documentación, dashboard propietario

  Fase 3: IA & Data   Meses 11-15    Inteligencia y automatización               Asistente IA, tasación, Big Data público, heatmaps, alertas predictivas

  Fase 4: Scale       Meses 16-18    Crecimiento y mobile                        App móvil, API datos, expansión geográfica, partnerships agencias
  ------------------------------------------------------------------------------------------------------------------------------------------------------------

**5.1 Fase 1: MVP (Meses 1-6)**

+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **Objetivo**                                                                                                                                                                              |
|                                                                                                                                                                                           |
| Lanzar un producto funcional que permita publicar inmuebles, buscarlos en mapa, crear perfiles de usuario y obtener un scoring básico. Validar la propuesta de valor con usuarios reales. |
+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

**Sprint 1-2 (Semanas 1-4): Fundamentos**

-   Setup de infraestructura cloud, CI/CD, entornos

-   Modelo de datos inicial (User, Property, Listing)

-   Autenticación básica (email/password + Google OAuth)

-   Diseño del design system y componentes base

**Sprint 3-4 (Semanas 5-8): Core Inmuebles**

-   Publicación de anuncios (formulario multi-paso)

-   Subida de fotos con compresión nativa en navegador y storage en Base de Datos

-   Página de resultados con filtros básicos

-   Ficha de inmueble con galeria

**Sprint 5-6 (Semanas 9-12): Mapa y Búsqueda**

-   Integración de mapa interactivo con pin exacto de ubicación

-   Geocodificación de direcciones

-   Búsqueda geoespacial en backend (PostGIS)

-   Filtros avanzados completos

**Sprint 7-8 (Semanas 13-16): Perfiles y Scoring Básico**

-   Perfil de usuario público

-   Subida de documentos cifrada (versión simple)

-   Scoring inicial basado en verificación de identidad y perfil completado

-   Badgess de \'Verificado\', \'Documentación subida\'

**Sprint 9-10 (Semanas 17-20): Mensajería y Dashboard**

-   Mensajería interna propietario-buscador

-   Dashboard propietario v1 (anuncios activos, nº de visitas, contactos)

-   Sistema de alertas de nuevos inmuebles

**Sprint 11-12 (Semanas 21-24): Lanzamiento MVP**

-   Pruebas de carga y rendimiento

-   Programa beta cerrada con 50-100 usuarios

-   Onboarding optimizado y emails de activación

-   Lanzamiento público con estrategia de adquisición inicial

**5.2 Fase 2: Confianza y Reputación (Meses 7-10)**

+----------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **Objetivo**                                                                                                                                                         |
|                                                                                                                                                                      |
| Activar el diferenciador clave: el sistema de reputación bidireccional y el KYC avanzado. En esta fase la plataforma empieza a crear el \'efecto red\' de confianza. |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------+

-   Flujo completo de reseñas post-contrato (invitación, redacción, publicación, respuesta)

-   KYC avanzado con Onfido o Veriff (verificación biométrica)

-   Parser de nóminas con OCR para extracción automática de datos de ingresos

-   Scoring compuesto v2 con todos los componentes definidos

-   Dashboard propietario v2: perfil demográfico de visitantes, scoring medio de interesados

-   Filtro de solicitudes por score mínimo para propietarios

-   Historial público de reseñas en perfil de usuario

-   Análitica interna: conversión de usuarios que se verifican vs los que no

**5.3 Fase 3: Inteligencia Artificial y Big Data (Meses 11-15)**

+----------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **Objetivo**                                                                                                                                                   |
|                                                                                                                                                                |
| Convertir los datos acumulados en la plataforma en ventaja competitiva. La IA pasa de ser una característica a ser un asistente ubicuo en toda la experiencia. |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------+

-   Asistente de búsqueda por lenguaje natural

-   Generador de anuncios con IA (título + descripción desde fotos y datos)

-   Tasador IA (AVM) con datos propios de la plataforma

-   Chatbot 24/7 para buscadores

-   Big Data Dashboard público: índices de precio, heatmaps, tendencias

-   Pipeline de datos (ETL) y data warehouse

-   Moderación automática de anuncios y reseñas con IA

-   Detección de fraude automática (anuncios duplicados, documentos falsos)

-   Alertas predictivas para propietarios (\'tu precio está por encima del mercado\')

**5.4 Fase 4: Escala y Expansión (Meses 16-18)**

+------------------------------------------------------------------------------------------------------------------------+
| **Objetivo**                                                                                                           |
|                                                                                                                        |
| Maximizar el alcance con la app móvil, abrir el ecosistema con una API de datos y preparar la expansión internacional. |
+------------------------------------------------------------------------------------------------------------------------+

-   App móvil iOS + Android (React Native) con funcionalidad completa

-   API de datos de mercado pública (modelo freemium para medios e investigadores)

-   Programa de partnerships con agencias inmobiliarias (acceso API para subida masiva de anuncios)

-   Herramientas para profesionales: comparativa de carteras, ROI estimado por inmueble

-   Integración con firmas digitales (DocuSign/Signaturit) para contratos

-   Expansión a Portugal y/o LATAM (Argentina, México)

**6. Stack Tecnológico Recomendado**

**6.1 Frontend**

  ---------------------------------------------------------------------------------------------------------------------------
  **Capa**            **Tecnología**             **Justificación**
  ------------------- -------------------------- ----------------------------------------------------------------------------
  Framework           Angular (última versión)   Arquitectura robusta, TypeScript nativo, SSR integrado, ideal para apps complejas

  Estilos             Tailwind CSS + Angular CDK Velocidad de desarrollo, design tokens, accesibilidad out-of-the-box

  Mapas               Mapbox GL JS               Mayor flexibilidad de estilo, precio competitivo, soporte de 3D y clusters

  Estado global       NgRx o Signals             Gestión de estado reactiva estándar en el ecosistema Angular

  Formularios         Angular Reactive Forms     Validación robusta nativa del framework

  Mobile              React Native + Expo        Código compartido con web, acceso a APIs nativas, OTA updates
  ---------------------------------------------------------------------------------------------------------------------------

**6.2 Backend**

  -----------------------------------------------------------------------------------------------------------------------
  **Capa**            **Tecnología**               **Justificación**
  ------------------- ---------------------------- ----------------------------------------------------------------------
  API Framework       Java + Spring Boot           Ecosistema robusto y empresarial, alta escalabilidad y rendimiento seguro

  Lenguaje            Java (Backend), TS (Front)   Java en backend para sólida OOP, TypeScript en el frontend con Angular

  Base de Datos       PostgreSQL + PostGIS         Relacional + geoespacial. Estándar de la industria para PropTech.

  Búsqueda            Elasticsearch (OpenSearch)   Full-text search, filtros complejos y alta velocidad para listings

  Caché               Redis                        Sesión, caché de queries frecuentes, rate limiting, pub/sub

  Cola de mensajes    RabbitMQ / Apache Kafka      Procesado asíncrono: scoring, notificaciones (muy maduro en ecosistema Java)

  ORM                 Hibernate (JPA)              Estándar en Java para persistencia relacional con integración fluida en Spring
  -----------------------------------------------------------------------------------------------------------------------

**6.3 Infraestructura Cloud (AWS Recomendado)**

  -------------------------------------------------------------------------------------------------------------
  **Servicio**        **AWS**                      **Propósito**
  ------------------- ---------------------------- ------------------------------------------------------------
  Compute             EKS (Kubernetes)             Orquestación de microservicios con auto-scaling

  Almacenamiento      PostgreSQL LOB / S3          Imágenes y vídeos local (DB) / KYC y Docs en S3

  Base de Datos       RDS PostgreSQL (Multi-AZ)    Alta disponibilidad con failover automático

  Email               SES (Simple Email Service)   Transaccional y marketing. Bajo coste, alta entregabilidad

  Logs y Monitoring   CloudWatch + Datadog         Observabilidad, alertas y trazas distribuidas

  Seguridad           KMS + Secrets Manager        Gestión de claves de cifrado y credenciales

  IaC                 Terraform                    Infraestructura como código, reproducible y versionada
  -------------------------------------------------------------------------------------------------------------

**6.4 IA y Machine Learning**

  ----------------------------------------------------------------------------------------------------------------------------------------
  **Propósito**                        **Solución**                               **Notas**
  ------------------------------------ ------------------------------------------ --------------------------------------------------------
  LLM (chatbot, redacción, tasación)   Anthropic Claude API (claude-sonnet-4-5)   Mejor relación calidad-coste para tareas inmobiliarias

  Embeddings para búsqueda semántica   text-embedding-3 (OpenAI)                  Buscar inmuebles por descripción semántica

  OCR de documentos                    AWS Textract                               Extracción de datos de nóminas y contratos

  KYC / Verificación biométrica        Onfido o Veriff                            Verificación de identidad regulada (AMLD5)

  Vector DB para búsqueda IA           Pinecone o pgvector                        Almacenamiento de embeddings para búsqueda semántica

  Detección de fraude en imágenes      Amazon Rekognition                         Detectar fotos de stock o imágenes manipuladas
  ----------------------------------------------------------------------------------------------------------------------------------------

**6.5 Seguridad de Documentos Sensibles**

+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **🔒 Arquitectura de Seguridad Documental**                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Los documentos financieros (nóminas, declaraciones de renta, contratos laborales) son datos sensibles bajo GDPR Art. 9. La arquitectura de seguridad tiene tres capas: (1) Cifrado en reposo con KMS de AWS usando claves por usuario; (2) Pre-signed URLs con tiempo de expiración de 15 minutos para acceso; (3) Audit trail completo de cada acceso con IP, timestamp y usuario solicitante. El usuario puede revocar el acceso en cualquier momento desde su panel. |
+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

**7. Equipo Recomendado y Riesgos**

**7.1 Equipo MVP (Fase 1)**

  -----------------------------------------------------------------------------------------------------
  **Rol**                     **Dedicación**    **Responsabilidades clave**
  --------------------------- ----------------- -------------------------------------------------------
  Tech Lead / Arquitecto      Full-time         Decisiones de arquitectura, API design, code review

  Backend Developer           Full-time         Backend: API (Spring Boot), DB, servicios core

  Frontend Developer          Full-time         Frontend: Angular, componentes, integración de mapa

  Product Manager             Full-time         Roadmap, priorización, análisis de usuarios, métricas

  Diseñador UX/UI             Full-time         Design system, flujos, prototipos, testing

  DevOps / Infra              Part-time (50%)   CI/CD, cloud setup, seguridad, monitoring

  Especialista Legal / GDPR   Consultoría       Contratos, política de privacidad, cumplimiento GDPR
  -----------------------------------------------------------------------------------------------------

**7.2 Ampliación del Equipo (Fases 2-4)**

-   Data Engineer (Fase 3): Pipeline ETL, data warehouse, Big Data

-   ML Engineer (Fase 3): Modelos de scoring, deteción de fraude, AVM

-   Mobile Developer (Fase 4): React Native, publicación en stores

-   Growth/SEO Specialist (Fase 2): Estrategia de adquisición orgánica

-   Customer Success (Fase 2): Onboarding de agencias y propietarios

**7.3 Principales Riesgos y Mitigaciones**

  -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  **Riesgo**                                                                            **Probabilidad**   **Impacto**   **Mitigación**
  ------------------------------------------------------------------------------------- ------------------ ------------- --------------------------------------------------------------------------------------------
  Problema del \'huevo y la gallina\': sin propietarios no hay buscadores y viceversa   Alta               Alto          Arrancar con una ciudad foco. Ofrecer publicación gratuita ilimitada el primer año.

  Resistencia a subir documentos por privacidad                                         Alta               Alto          Hacer el scoring opcional pero incentivado. Mostrar tasa de éxito de perfiles verificados.

  Fraude en reseñas (falsas valoraciones)                                               Media              Alto          Reseñas solo post-contrato verificado. Moderación IA + humana.

  Incumplimiento GDPR (datos financieros)                                               Baja               Crítico       Arquitectura privacy-by-design, DPO externo, auditorías trimestrales.

  Costes de IA escalando con el uso                                                     Media              Medio         Caché agresiva, rate limiting por plan, optimización de prompts.

  Reacción de Idealista (guerra de precios o copia)                                     Media              Alto          Construir el efecto red de reputación rápido. Es difícil de copiar.
  -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**8. Modelo de Negocio y Métricas Clave**

**8.1 Flujos de Monetización**

  ---------------------------------------------------------------------------------------------------------------------------------------------------
  **Fuente de ingresos**            **Modelo**           **Descripción**
  --------------------------------- -------------------- --------------------------------------------------------------------------------------------
  Subscripción Profesional          SaaS mensual/anual   Agencias y propietarios con múltiples inmuebles: analytics avanzados, CRM, integración API

  Listings Destacados               Pay-per-listing      Anuncios en posiciones premium del mapa y resultados

  Verificación Premium (Buscador)   Pago único           El buscador paga por obtener un score verificado y subir documentación

  Informes de Mercado               Freemium             Dashboard público gratuito; informes detallados de pago para profesionales

  API de Datos                      Uso por llamada      Acceso a datos agregados para portales, medios e inversores
  ---------------------------------------------------------------------------------------------------------------------------------------------------

**8.2 KPIs del Producto (North Star)**

-   North Star Metric: Núm. de contratos de alquiler/venta iniciados a través de la plataforma en el mes

-   Listings activos verificados (con dirección exacta + al menos 1 foto real)

-   \% de usuarios con perfil de confianza completado (≥ Nivel Plata)

-   Tasa de respuesta de propietarios a solicitudes (objetivo: \>80% en \<24h)

-   NPS separado para propietarios y para buscadores

-   Tasa de fraude detectado (anuncios falsos / total)

-   CAC (Coste de adquisición) vs LTV (Valor de vida del cliente)

**9. Resumen Ejecutivo del Timeline**

  ---------------------------------------------------------------------------------------------------------------
  **Mes**   **Fase**        **Hito**             **Descripción**
  --------- --------------- -------------------- ----------------------------------------------------------------
  1-2       F1: MVP         Fundamentos          Infra, CI/CD, modelo de datos, auth, diseño

  3-4       F1: MVP         Core inmuebles       Publicación de anuncios, fotos, fichas, filtros

  5-6       F1: MVP         Mapa y lanzamiento   Mapa interactivo, scoring básico, beta cerrada, go-live MVP

  7-8       F2: Confianza   KYC y docs           Verificación biométrica, parser nóminas, scoring v2

  9-10      F2: Confianza   Reseñas              Sistema de reseñas bidireccional, dashboard propietario v2

  11-13     F3: IA          Asistencia IA        Chatbot, generador anuncios, tasador IA, moderación automática

  14-15     F3: IA          Big Data             Pipeline ETL, dashboard de mercado público, heatmaps

  16-18     F4: Scale       Mobile & API         App móvil iOS/Android, API datos, partnerships agencias
  ---------------------------------------------------------------------------------------------------------------

*Documento elaborado con metodología PropTech Product Management*

*Versión 1.0 --- Marzo 2026 --- Confidencial*
