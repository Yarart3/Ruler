# Sprint Review — Sprint 04

## Resultados obtenidos

**Sprint Goal:**  
_Conectar la app con una API remota de reservas de hotel, persistir localmente los datos relevantes, añadir una galería de imágenes por viaje y dejar cerrada la gestión de reservas dentro de la arquitectura MVVM con Room y Hilt._

**Comparación con Sprint Goal:**  
_Se ha completado la integración remota de hoteles con Retrofit, la persistencia local de reservas e imágenes con Room, y la actualización de la experiencia de usuario para buscar hoteles, reservar, asignar reservas a viajes, ver galerías y cancelar reservas._

---

## 1. Tareas completadas

| ID | Tarea | Completada | Comentarios |
|----|-------|------------|-------------|
| T1.1 | Añadir dependencia Retrofit y configurar el cliente HTTP | Sí | Cliente HTTP y `HotelApiService` integrados con Hilt |
| T1.2 | Crear modelos de datos e interfaces API para la API de hoteles | Sí | DTOs, mappers y modelos de dominio separados |
| T1.3 | Crear capa Repository para abstraer el uso de la API | Sí | `HotelRepositoryImpl` encapsula acceso remoto |
| T1.4 | Crear tests unitarios mockeando la conexión remota | Sí | Tests del repositorio remoto y cancelación local/remota |
| T2.1 | Crear pantalla de búsqueda de hoteles | Sí | Selector de ciudad y fechas con `DatePicker` |
| T2.2 | Mostrar lista de hoteles y habitaciones devueltos por la API | Sí | Resultados con habitaciones y acción de reserva |
| T2.3 | Implementar reserva de habitación y persistir info localmente en tabla de viajes | Sí | Reserva guardada localmente y reflejada en el viaje al asignarla |
| T2.4 | Mostrar todas las imágenes del hotel y habitaciones en la pantalla de reserva | Sí | Portada del hotel y galería de imágenes de habitaciones |
| T3.1 | Permitir al usuario adjuntar múltiples imágenes a un viaje | Sí | Selección múltiple desde el detalle del viaje |
| T3.2 | Guardar imágenes localmente en el dispositivo | Sí | Copia física a almacenamiento interno + metadatos en Room |
| T3.3 | Mostrar galería de imágenes por viaje en la pantalla de detalle | Sí | Galería por viaje con visor a pantalla completa |
| T4.1 | Crear pantalla para listar todas las reservas locales indicando el viaje relacionado | Sí | Pantalla principal de hoteles/reservas unificada |
| T4.2 | Añadir funcionalidad para eliminar una reserva localmente y via API | Sí | Cancelación remota y limpieza local sincronizada |
| T4.3 | Mostrar imágenes del hotel y habitación asociadas en el listado de reservas | Sí | Mini-galería por reserva en la lista |
| T4.4 | Actualizar pantalla "My Trips" para indicar si un viaje tiene reserva de hotel y mostrar detalles | Sí | Badge en Home y bloque de reserva en detalle del viaje |

---

## 2. Validación del DoD

| Criterio | Estado | Evidencia |
|---------|--------|-----------|
| Retrofit configurado y apuntando a la API | Sí | `NetworkModule`, `HotelApiService`, `BuildConfig.HOTEL_API_BASE_URL` |
| Modelos de datos e interfaces API en estructura MVVM | Sí | `domain`, `data/remote/dto`, `data/remote/api`, `data/remote/mapper` |
| Repository remoto abstrae la API | Sí | `HotelRepositoryImpl` |
| Tests unitarios de conexión remota mockeada | Sí | `HotelRepositoryImplTest` |
| Pantalla de búsqueda de hoteles con ciudad y fechas | Sí | `HotelSearchScreen` |
| Lista de hoteles y habitaciones mostrada correctamente | Sí | `HotelSearchScreen` |
| Reserva persistida localmente | Sí | `LocalHotel`, `local_hotels`, asignación al viaje |
| Imágenes de hotel y habitación visibles en la reserva | Sí | `HotelBookingScreen` |
| Adjuntar múltiples imágenes a un viaje | Sí | `TripDetailsScreen` + `TripImageViewModel` |
| Imágenes guardadas localmente | Sí | `TripImageRepositoryImpl` |
| Galería propia por viaje | Sí | `TripDetailsScreen` + `GalleryScreen` |
| Pantalla de reservas locales con viaje asociado e imágenes | Sí | `ReservationsScreen` |
| Eliminación local y remota de reservas | Sí | `LocalHotelViewModel.deleteHotel` |
| "My Trips" refleja reserva y muestra detalles | Sí | `HomeScreen` + `TripDetailsScreen` |
| Hilt y Room usados para DI y persistencia | Sí | `di/*`, `RulerDatabase` |
| Estructura en capas presente | Sí | `ui`, `ui/viewmodels`, `data`, `data/repository`, `di`, `domain` |
| Fechas/horas con pickers en los flujos relevantes | Sí | Viajes, actividades y hoteles usan `DatePicker` / `TimePicker` |

---

## 3. Verificación técnica

Se ha validado el estado final del sprint con las siguientes tareas:

- `./gradlew --no-daemon :app:compileDebugKotlin`
- `./gradlew --no-daemon testDebugUnitTest`
- `./gradlew --no-daemon assembleDebug`

**Resultado:** todas correctas en ejecución serie.

**Nota técnica:** en este proyecto, lanzar varias tareas de Gradle en paralelo puede provocar fallos falsos de `NoClassDefFoundError` en tests Robolectric. Ejecutadas en serie, las comprobaciones pasan correctamente.

---

## 4. Desviaciones

_Durante el sprint acabamos simplificando la UX de hoteles en una sola pantalla principal que combina las funciones de búsqueda, reservas locales, edición, asignación y cancelación. El PDF no exigía dos pantallas separadas, así que se priorizó una experiencia más coherente._

_También se cambió la estrategia inicial de “crear un viaje nuevo automáticamente al reservar” por una más consistente con el uso real de la app: la reserva se guarda primero como reserva local y, si el usuario la vincula a un viaje o reservó desde un viaje concreto, ese viaje pasa a reflejar también la reserva completa._

---

## 5. Retrospectiva

**Qué funcionó bien**
- La integración Retrofit + Room + Hilt quedó estable sin romper la arquitectura previa
- La persistencia de imágenes por viaje quedó limpia y desacoplada del resto del dominio
- La unificación de la pantalla de hoteles/reservas mejoró bastante la experiencia de uso

**Qué no funcionó**
- La API externa no siempre fue fiable durante el desarrollo, lo que obligó a apoyarse más en tests locales y validación por capas
- La sincronización entre reserva remota, reserva local y reflejo visual en viajes necesitó varias iteraciones hasta quedar consistente

**Qué mejoraríamos en un siguiente sprint**
- Añadir tests instrumentados de UI para los flujos de reserva, asignación y galería
- Refinar todavía más la separación entre “hotel asignado” y “reserva confirmada” a nivel de modelo de dominio y etiquetas visuales

---

## 6. Autoevaluación del equipo (0-10)

**Nota: 9**

**Justificación:**  
_El sprint queda funcionalmente completo y técnicamente estable: integra persistencia remota, persistencia local, galerías y gestión de reservas sin romper lo que ya existía. No le damos el 10 porque la validación sigue descansando mucho en tests unitarios y comprobación manual, y porque la sincronización entre estados locales y remotos ha sido el punto más delicado de toda la entrega._
