---
description: Workflow estricto para modificar la base de datos a través de migraciones de JPA y Liquibase
---

# Flujo: Migración Base de Datos

**Rol objetivo:** Spring Architect + DevOps

Para evitar roturas en bases de datos relacionales ya desplegadas en test/producción, alterar columnas jamás debe hacerse por "Hibernate Auto-Update (*update*/*create-drop*)".

1. **Actualiza tu Entity:** Añade o remueve la propiedad e incluye anotaciones `@Column` u otras validaciones pertinentes.
2. **Generación del Changelog:** Usa la herramienta de la CLI o Skill de Maven/Gradle para ejecutar el autogenerador de *Liquibase/Flyway*, validando diferencias.
3. **Revisión Humana:** El `DevOps Specialist` revisa que el archivo `.xml` (o `.yml`) autogenerado no contenga acciones destructivas catastróficas (Ej. borrar una tabla de transacciones de pago).
4. **Verificación de Rollbacks:** Garantiza que se haya generado o escrito manualmente el bloque `<rollback>` correspondiente en el Changelog.
5. **Aprobación final** para enviar a entorno de PR.
