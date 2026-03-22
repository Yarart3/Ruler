# Sprint Review — Sprint 02

## Resultados obtenidos

**Sprint Goal:**  
_Implementar la lógica real de la app con arquitectura MVVM, CRUD de viajes y actividades, persistencia de preferencias y soporte multi-idioma._

**Comparación con Sprint Goal:**  
_Se ha avanzado en la lógica real de la aplicación y se ha dejado montada una base funcional con MVVM, CRUD en memoria, preferencias persistentes y cambio de idioma, aunque integrar todo esto sobre la navegación heredada del Sprint 1 ha sido más costoso de lo esperado._

---

## 1. Tareas completadas

| ID | Tarea | Completada | Comentarios |
|----|-------|------------|-------------|
| T1.1 | CRUD de viajes inMemory (`addTrip`, `editTrip`, `deleteTrip`) | Sí | Implementado y probado |
| T1.2 | CRUD de actividades inMemory (`addActivity`, `updateActivity`, `deleteActivity`) | Sí | Implementado y probado |
| T1.3 | Validación de fechas con `DatePicker` | Sí | Evitó errores de formato |
| T1.4 | Pantalla de preferencias con `SharedPreferences` (`username`, `dateOfBirth`, `darkMode`, `language`) | Sí | Persistencia funcional |
| T1.5 | Multi-idioma mínimo 3 idiomas (`en`, `ca`, `es`) | Sí | Cambio de idioma operativo |
| T2.1 | Estructura Menu -> Travel -> Itinerary (CRUD) | Sí | Flujo conectado |
| T2.2 | UI funcional para añadir y modificar viajes y actividades | Sí | Formularios operativos |
| T2.3 | Actualizaciones dinámicas en las listas con `StateFlow` | Sí | Sincronización correcta |
| T3.1 | Validación de inputs en `ViewModel` y UI con mensajes de error | Sí | Mensajes básicos implementados |
| T3.2 | Tests unitarios para CRUD de viajes y actividades | Sí | Cobertura básica |
| T3.3 | Simular interacciones y registrar errores | Sí | Se detectaron fallos de integración |
| T3.4 | Documentación actualizada con resultados de tests | Sí | Documentación del sprint al día |
| T3.5 | Logs en `Logcat` con buenas prácticas | Sí | Útiles para depurar |

---

## 2. Desviaciones

_La navegación manual heredada del Sprint 1 complicó bastante más de lo previsto la conexión del `ViewModel` con las pantallas. Sobre el papel parecía un cambio directo, pero al no usar todavía `NavController`, pasar estado y mantener consistencia entre vistas acabó siendo bastante más incómodo._

_Además, tuvimos que crear modelos legacy temporales para no romper algunas pantallas antiguas mientras íbamos migrando la lógica nueva. No era la solución ideal, pero fue la manera más práctica de avanzar sin desmontar lo que ya funcionaba visualmente._

---

## 3. Retrospectiva

**Qué funcionó bien**
- La separación en capas MVVM quedó bastante limpia y nos ayudó a ordenar mejor el proyecto
- `StateFlow` actualizó las pantallas automáticamente sin darnos problemas importantes
- Los `DatePicker` evitaron muchos errores de formato que con texto manual habrían salido seguro

**Qué no funcionó**
- Conectar el `ViewModel` a pantallas ya existentes sin romper el diseño fue más costoso de lo esperado
- La navegación manual con strings se volvió difícil de mantener en cuanto la app empezó a tener más estado y más pantallas conectadas

**Qué mejoraremos en el Sprint 3**
- Usar Navigation Compose con `NavController` para dejar de depender de navegación manual
- Implementar persistencia real con Room en lugar de seguir apoyándonos en datos en memoria

---

## 4. Autoevaluación del equipo (0-10)

**Nota: 8**

**Justificación:**  
_El sprint ha salido bien porque hemos pasado de una app sobre todo visual a una base bastante más real y funcional. La arquitectura quedó mejor de lo que esperábamos y los cambios de estado respondieron bien. Aun así, no nos ponemos más nota porque la integración con pantallas heredadas fue bastante más torpe de lo que pensábamos y seguimos arrastrando decisiones del Sprint 1 que ahora nos penalizan._
