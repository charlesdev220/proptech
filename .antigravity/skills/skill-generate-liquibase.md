---
name: Liquibase Auto-Generator
description: Habilidad para comparar el Entity model de Spring Data JPA con la DB y sacar XMLs.
---

# Skill: Liquibase Changelog Generation

Si el desarrollador de Java Spring altera una clase `@Entity`, necesitas usar el Liquibase Maven Plugin para generar la migración diferencial sin tocar la redacción en SQL puro a mano a no ser que sea inevitable:

1. Modificas la Entidad en el IDE de acuerdo al Prompt.
2. Usas un script asociado o invocas un bash wrapper (como `mvn liquibase:diff`) para comparar contra DB Local.
3. Generas un archivo preformateado `.xml` o `.yaml` e incluyes sus cambios en el `.antigravity/workflows/wf-database-migration.md` para checklist.
4. Jamás aplicar un drop-table automático desde Liquibase en un esquema de producción sin doble verificación.
