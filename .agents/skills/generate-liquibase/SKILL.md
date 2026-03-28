---
name: generate-liquibase
description: Habilidad para automatizar la generación de changelogs de Liquibase comparando el modelo de entidades JPA con la base de datos local.
---

# Liquibase Auto-Generator

Instrucciones para generar migraciones diferenciales utilizando el Liquibase Maven Plugin tras la modificación de entidades de Spring Data JPA.

## When to use this skill

- Utiliza esta habilidad cuando el desarrollador de Java Spring altera una clase `@Entity`.
- Esto es útil para generar migraciones diferenciales de base de datos (`.xml` o `.yaml`) automáticamente sin necesidad de redactar SQL puro a mano (a menos que sea estrictamente necesario).

## How to use it

1. **Modificación de Entidad:** Modifica la Entidad en el IDE de acuerdo con los requerimientos recibidos.
2. **Generación de Diferencias:** Utiliza el script asociado o invoca el wrapper (como `mvn liquibase:diff`) para comparar el modelo de entidades actualizado contra la base de datos local.
3. **Registro de Cambios:** Genera el archivo preformateado `.xml` o `.yaml` e incluye sus cambios correspondientes en el checklist dentro de `.antigravity/workflows/wf-database-migration.md`.
4. **Verificación Estricta:** Jamás apliques un `drop-table` automático desde Liquibase en un esquema de producción sin doble verificación. Siempre revisa cuidadosamente el changelog generado antes de confirmarlo.
