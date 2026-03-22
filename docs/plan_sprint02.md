# Sprint 02 – Planning Document

## 1. Sprint Goal

En este segundo sprint queremos dejar atrás el prototipo visual del Sprint 1 y empezar a implementar la lógica real de la app. La idea es montar una arquitectura MVVM básica pero funcional, hacer el CRUD de viajes y actividades en memoria, guardar las preferencias del usuario con persistencia y añadir soporte multi-idioma como mínimo en castellano, catalán e inglés.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T1.1 | CRUD de viajes inMemory (`addTrip`, `editTrip`, `deleteTrip`) | Nel         | 3 | Muy alta |
| T1.2 | CRUD de actividades inMemory (`addActivity`, `updateActivity`, `deleteActivity`) | Nel         | 3 | Muy alta |
| T1.3 | Validación de fechas con `DatePicker` | Nel         | 2 | Alta |
| T1.4 | Pantalla de preferencias con `SharedPreferences` (`username`, `dateOfBirth`, `darkMode`, `language`) | Gerard      | 3 | Muy alta |
| T1.5 | Multi-idioma mínimo 3 idiomas (`en`, `ca`, `es`) | Gerard      | 2.5 | Alta |
| T2.1 | Estructura Menu -> Travel -> Itinerary (CRUD) | Nel         | 3 | Muy alta |
| T2.2 | UI funcional para añadir y modificar viajes y actividades | Nel         | 4 | Muy alta |
| T2.3 | Actualizaciones dinámicas en las listas con `StateFlow` | Nel         | 3 | Alta |
| T3.1 | Validación de inputs en `ViewModel` y UI con mensajes de error | Gerard      | 3 | Alta |
| T3.2 | Tests unitarios para CRUD de viajes y actividades | Nel         | 3 | Media |
| T3.3 | Simular interacciones y registrar errores | Gerard      | 2 | Media |
| T3.4 | Documentación actualizada con resultados de tests | Gerard      | 1.5 | Media |
| T3.5 | Logs en `Logcat` con buenas prácticas | Gerard      | 1.5 | Baja |

---

## 3. Definition of Done (DoD)

- [ ] La app sigue compilando y funcionando sin errores después de introducir la arquitectura MVVM
- [ ] Se pueden crear, editar y borrar viajes en memoria desde la interfaz
- [ ] Se pueden crear, editar y borrar actividades asociadas a un viaje
- [ ] Las listas de viajes y actividades se actualizan correctamente al modificar el estado
- [ ] La pantalla de preferencias guarda y recupera `username`, `dateOfBirth`, `darkMode` y `language`
- [ ] La app permite cambiar como mínimo entre castellano, catalán e inglés
- [ ] Los formularios validan datos incorrectos y muestran mensajes de error entendibles
- [ ] Hay tests unitarios básicos para comprobar el CRUD de viajes y actividades
- [ ] La documentación del sprint queda actualizada con lo que se ha hecho de verdad

---

## 4. Riesgos identificados

* **MVVM es nuevo para nosotros:** entendemos la idea general, pero al aplicarlo de verdad pueden aparecer dudas con la responsabilidad de cada capa.
* **Gestión del estado con `StateFlow`:** si no lo conectamos bien con la UI, es fácil que haya pantallas que no se refresquen como esperamos o que se actualicen demasiado.
* **Coordinación entre capas `domain` / `data` / `ui`:** si no mantenemos una separación clara, podemos acabar mezclando lógica y complicando el proyecto justo cuando empieza a crecer.
