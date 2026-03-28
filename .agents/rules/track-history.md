---
trigger: always_on
---
---
description: Regla obligatoria para llevar el historial de implementacion como un Log secuencial "append-only".
---

# Seguimiento Estricto del Historial de Implementación

**Aprendizaje Clave (Lección de Implementaciones Previas):** 
NUNCA se debe sobrescribir borrar ni reescribir el contenido previo del archivo `HISTORIAL_IMPLEMENTACION.md`. El archivo funciona como un *journal* o bitácora de novedades inversas (lo más reciente va primero). 

Debes apegarte estrictamente a las siguientes normas al actualizar el historial:

1. **Inserción en la Parte Superior (Prepend):** 
   - Abre el archivo `HISTORIAL_IMPLEMENTACION.md`.
   - Busca la línea donde termina la cabecera principal (generalmente debajo de la cita `> Registro secuencial...`).
   - INSERTA un nuevo bloque de texto justo debajo de esa cabecera con el registro de las funcionalidades completadas en tu turno de trabajo.
   - NO alteres ni resumas absolutamente nada del contenido que ya estaba documentado debajo. Mantenlo intacto.

2. **Estructura Requerida para el Módulo Insertado:**
   Siempre añade tu actualización usando EXACTAMENTE esta estructura Markdown:
   
   ```markdown
   ### Qué hemos completado hasta ahora ([Breve título de lo que has hecho]):
   *Fase actual:* [Ej: Fase 1: MVP - 1.2 Core de Gestión de Inmuebles]
   *Estado actual:* [En Proceso / Completado]
   - ✔️ **[Nombre de la Tarea/Atributo]:** [Descripción técnica de 1 línea]
   ```

3. **Reporte de Transparencia (En tu respuesta al Humano):**
   Al concluir tu turno y generar la respuesta, especifica claramente que has "Añadido una nueva entrada al principio del historial de implementación", indicando la Tarea y la Fase.
