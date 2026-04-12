# Sprint 03 – Planning Document

## 1. Sprint Goal

En este tercer sprint queremos dotar a la app de persistencia real y de un sistema de usuarios completo. La idea es reemplazar el almacenamiento en memoria del Sprint 02 con Room/SQLite, integrar Firebase para autenticación (login, registro y recuperación de contraseña) y asegurarnos de que cada usuario solo ve sus propios viajes. También crearemos un log de accesos, añadiremos tests unitarios para las capas de datos y dejaremos todo bien documentado en `design.md`.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T1.1 | Crear clase Room Database con Hilt como DI | Nel | 1.5 | Muy alta |
| T1.2 | Definir Entities `Trip` e `ItineraryItem` (datetime, text, int) | Nel | 1 | Muy alta |
| T1.3 | Crear DAOs para `Trip` e `ItineraryItem` | Nel | 1.5 | Muy alta |
| T1.4 | Implementar CRUD completo en los DAOs | Nel | 2 | Muy alta |
| T1.5 | Adaptar ViewModels para usar Room en lugar de almacenamiento en memoria | Nel | 2 | Muy alta |
| T1.6 | Verificar que la UI se actualiza correctamente ante cambios en la BD | Gerard | 1.5 | Alta |
| T2.1 | Conectar la app a Firebase | Nel | 1 | Muy alta |
| T2.2 | Diseñar pantalla de login (formulario Android) | Gerard | 1.5 | Muy alta |
| T2.3 | Implementar autenticación email/password con Firebase | Nel | 2 | Muy alta |
| T2.4 | Crear acción de logout y redirigir al login | Gerard | 1 | Alta |
| T2.5 | Registrar operaciones y errores de autenticación con Logcat | Gerard | 0.5 | Media |
| T3.1 | Diseñar pantalla de registro (formulario Android) | Gerard | 1.5 | Muy alta |
| T3.2 | Implementar registro con Firebase usando patrón Repository + verificación de email | Nel | 2.5 | Muy alta |
| T3.3 | Diseñar e implementar pantalla + lógica de recuperación de contraseña | Gerard | 2 | Alta |
| T4.1 | Crear tabla `User` en Room con los campos requeridos y validación de username único | Nel | 2 | Muy alta |
| T4.2 | Vincular tabla `Trip` a usuario y filtrar viajes por usuario activo | Nel | 2 | Alta |
| T4.3 | Actualizar `design.md` con el esquema de BD y estrategia de migración | Gerard | 1.5 | Media |
| T4.4 | Crear tabla de log de accesos (`userId` + `datetime` de login/logout) | Nel | 1.5 | Media |
| T5.1 | Escribir tests unitarios para DAOs e interacciones con BD | Nel | 2.5 | Alta |
| T5.2 | Implementar validación de datos (nombres duplicados, fechas válidas) | Gerard | 1.5 | Alta |
| T5.3 | Usar Logcat para trazar operaciones y errores de BD | Gerard | 1 | Media |
| T5.4 | Actualizar `design.md` con esquema final y ejemplos de uso de la BD | Gerard | 1 | Baja |

---

## 3. Definition of Done (DoD)

- [ ] La app compila y funciona sin errores con Room + Hilt integrados
- [ ] Las entidades `Trip` e `ItineraryItem` están definidas en Room con al menos un campo datetime, uno de texto y uno entero
- [ ] Se pueden crear, editar y borrar viajes e ítems de itinerario desde la interfaz, con persistencia real en SQLite
- [ ] Las listas se actualizan correctamente en la UI cuando la base de datos cambia
- [ ] Al abrir la app se comprueba si el usuario está autenticado; si no lo está, se muestra la pantalla de login
- [ ] El login, el registro y el logout funcionan correctamente con Firebase
- [ ] El registro incluye verificación de email y usa el patrón Repository
- [ ] Existe una pantalla funcional para recuperar la contraseña
- [ ] La tabla `User` persiste la información del usuario y valida que el username no esté duplicado
- [ ] Cada usuario solo ve sus propios viajes
- [ ] Existe una tabla de log que registra cada login y logout con `userId` y `datetime`
- [ ] Hay tests unitarios que cubren los DAOs y las operaciones principales de la BD
- [ ] Los formularios validan datos incorrectos y muestran mensajes de error claros
- [ ] `design.md` está actualizado con el esquema de base de datos, las migraciones y ejemplos de uso

---

## 4. Riesgos identificados

* **Room + Hilt juntos por primera vez:** la integración de ambas librerías a la vez puede generar conflictos de configuración o inyección difíciles de depurar al principio.
* **Firebase Authentication y estado de sesión:** gestionar correctamente el estado del usuario (sesión activa, caducada, no verificada) entre pantallas puede ser más complejo de lo que parece.
* **Patrón Repository con múltiples fuentes de datos:** ahora tenemos Room y Firebase a la vez; mantener una separación limpia entre las capas `data` y `domain` es más crítico que en sprints anteriores.
* **Migraciones de base de datos:** cualquier cambio en las Entities después de la primera ejecución requiere una migración explícita; si no se gestiona bien, la app puede crashear al actualizar.
* **Estimaciones optimistas:** hay más tareas interdependientes que en sprints anteriores (no se puede testear T4.2 sin tener T2.3 y T4.1 listos), lo que puede generar bloqueos si alguna tarea se retrasa.
