# Sprint 04 – Planning Document

## 1. Sprint Goal

En este cuarto sprint queremos conectar la app con el mundo exterior mediante persistencia remota. La idea es integrar Retrofit para consumir la API REST de reservas de hotel, implementar pantallas de búsqueda y reserva de habitaciones, gestionar una galería de imágenes por viaje guardada localmente, y permitir listar y cancelar reservas. Todo manteniendo la arquitectura MVVM con Repository, Room y Hilt que ya teníamos, y asegurándonos de que el sprint queda bien documentado y testeado.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T1.1 | Añadir dependencia Retrofit y configurar el cliente HTTP | Nel | 1 | Muy alta |
| T1.2 | Crear modelos de datos y interfaces API (estructura MVVM) para la API de hoteles | Nel | 2 | Muy alta |
| T1.3 | Crear capa Repository para abstraer el uso de la API | Gerard | 1.5 | Muy alta |
| T1.4 | Crear tests unitarios mockeando la conexión remota | Nel | 2 | Alta |
| T2.1 | Crear pantalla de búsqueda de hoteles (ciudad, fechas inicio y fin) | Gerard | 2 | Muy alta |
| T2.2 | Mostrar lista de hoteles y habitaciones devueltos por la API | Gerard | 1.5 | Muy alta |
| T2.3 | Implementar reserva de habitación y persistir info localmente en tabla de viajes | Nel | 2.5 | Muy alta |
| T2.4 | Mostrar todas las imágenes del hotel y habitaciones en la pantalla de reserva | Nel | 2 | Alta |
| T3.1 | Permitir al usuario adjuntar múltiples imágenes a un viaje | Gerard | 1.5 | Alta |
| T3.2 | Guardar imágenes localmente en el dispositivo (BD o almacenamiento) | Nel | 2 | Alta |
| T3.3 | Mostrar galería de imágenes por viaje en la pantalla de detalle del viaje | Gerard | 1.5 | Alta |
| T4.1 | Crear pantalla para listar todas las reservas locales indicando el viaje relacionado | Gerard | 1.5 | Alta |
| T4.2 | Añadir funcionalidad para eliminar una reserva localmente y via API | Nel | 2 | Alta |
| T4.3 | Mostrar imágenes del hotel y habitación asociadas en el listado de reservas | Gerard | 1.5 | Media |
| T4.4 | Actualizar pantalla "My Trips" para indicar si un viaje tiene reserva de hotel y mostrar detalles | Gerard | 1.5 | Media |

---

## 3. Definition of Done (DoD)

- [ ] Retrofit está configurado con el cliente HTTP y conecta correctamente con la API `http://15.224.84.148:8090`
- [ ] Existen modelos de datos e interfaces API siguiendo la estructura MVVM
- [ ] La capa Repository abstrae correctamente el acceso a la API remota
- [ ] Hay tests unitarios que mockean la conexión remota y cubren los principales casos
- [ ] Existe una pantalla de búsqueda de hoteles que permite seleccionar ciudad (Londres, París o Barcelona) y fechas mediante date pickers
- [ ] Se muestra la lista de hoteles y habitaciones (típicamente 3 por hotel) devuelta por la API
- [ ] El usuario puede reservar una habitación y la información de la reserva (ID, habitación, hotel, precio, etc.) queda persistida localmente
- [ ] Las imágenes del hotel y las habitaciones se muestran correctamente en la pantalla de reserva
- [ ] El usuario puede adjuntar múltiples imágenes a un viaje
- [ ] Las imágenes se guardan localmente en el dispositivo
- [ ] Cada viaje muestra su galería de imágenes propia en la pantalla de detalle
- [ ] Existe una pantalla que lista todas las reservas locales con su viaje asociado y las imágenes del hotel/habitación
- [ ] Se puede eliminar una reserva localmente (y vía API si aplica)
- [ ] La pantalla "Mis Viajes" indica si un viaje tiene reserva de hotel y muestra los detalles correspondientes
- [ ] Se usa HILT como librería de DI y ROOM para la persistencia local
- [ ] La estructura de carpetas incluye al menos: `view`, `viewmodel`, `repo`, `di` y `data`
- [ ] Todos los campos de fecha y hora usan date pickers

---

## 4. Riesgos identificados

* **Integración Retrofit + Room + Hilt a la vez:** combinar tres librerías con sus propias configuraciones de DI puede generar conflictos difíciles de rastrear, especialmente al inyectar el repositorio remoto junto al local.
* **Disponibilidad y estabilidad de la API externa:** al depender de un servidor externo, cualquier caída o cambio en la API puede bloquear el desarrollo de T2 y T4 por completo.
* **Gestión de imágenes locales:** guardar y recuperar imágenes del almacenamiento del dispositivo o la BD implica gestionar permisos, rutas y posibles problemas de rendimiento con galerías grandes.
* **Sincronización entre datos remotos y locales:** mantener la coherencia entre las reservas guardadas localmente y el estado real de la API (cancelaciones, cambios) puede ser complejo si no se define bien la estrategia desde el principio.
* **Dependencias entre tareas:** T2.3 y T2.4 dependen de que T1.1 y T1.2 estén completadas; T4.2 necesita T4.1 operativa. Un retraso inicial puede bloquear el resto de la entrega.
