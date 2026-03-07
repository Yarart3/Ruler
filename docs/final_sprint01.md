# Sprint Review — Sprint 01

## Resultados obtenidos

**Sprint Goal:**  
_Implementar las pantallas principales de la app Ruler con navegación funcional entre ellas._

**Comparación con Sprint Goal:**  
_Se ha completado la navegación entre todas las pantallas con datos mockeados._

---

## 1. Tareas completadas

| ID | Tarea | Completada | Comentarios |
|----|-------|------------|-------------|
| T1 | SplashScreen con animación | Sí | Sin problemas |
| T2 | HomeScreen con lista de viajes | Sí | Sin problemas |
| T3 | TripDetailScreen con timeline de actividades | Sí | Sin problemas |
| T4 | GalleryScreen | Sí | Sin problemas |
| T5 | PreferencesScreen | Sí | Sin problemas |
| T6 | AboutScreen | Sí | Sin problemas |
| T7 | TermsScreen | Sí | Sin problemas |
| T8 | ProfileScreen | Sí | Sin problemas |
| T9 | ActivityDetailScreen | Sí | Sin problemas |
| T10 | TripOptionsScreen | Sí | Sin problemas |
| T11 | NewTripScreen | Sí | Sin problemas |
| T12 | Navegación completa entre pantallas | Sí | Sin problemas |

---

## 2. Desviaciones

_Inicialmente intentamos crear composables reutilizables para la TopBar y la BottomBar para evitar duplicar código en cada pantalla. Sin embargo, al ser un prototipo con datos mockeados y al aumentar la complejidad de pasar parámetros de navegación entre composables, decidimos optar por copiar el código directamente en cada pantalla. Esto fue una decisión consciente para no sobrecomplicar el sprint._

_Además, varios iconos de `Icons.Default` usados en `PreferencesScreen` no existían en la librería (`AttachMoney`, `DarkMode`, `Language`), lo que generó errores inesperados que costaron tiempo de depuración._

---

## 3. Retrospectiva

**Qué funcionó bien**
- La estructura visual de las pantallas quedó consistente (topbar, bottombar, FAB)
- Los datos mockeados permitieron avanzar rápido sin depender de una base de datos
- Invertimos tiempo en buscar cómo hacer una animación atractiva para la SplashScreen y el resultado quedó visualmente cuidado
- El diseño del timeline de actividades con línea vertical y ticks quedó bien resuelto

**Qué no funcionó**
- Los iconos de Material Icons no están todos disponibles en `Icons.Default` — no hay forma fácil de saber cuáles existen sin probarlos, y esto causó varios errores de compilación inesperados
- Al añadir nuevos parámetros de navegación a las pantallas, el `MainActivity.kt` quedaba roto hasta que todas las pantallas estuvieran actualizadas — trabajar con errores en rojo dificultó el flujo
- Intentar abstraer la TopBar y BottomBar en un componente compartido consumió tiempo sin llegar a una solución limpia, y acabamos duplicando el código igualmente

**Qué mejoraremos en el próximo sprint**
- Usar Navigation Compose (con `NavController`) en lugar de gestionar la navegación manualmente con strings
- Investigar mejor los componentes disponibles antes de empezar a implementar, para evitar errores de iconos u otros
- Implementar persistencia de datos real y funcionalidad en los botones de editar y borrar

---

## 4. Autoevaluación del equipo (0-10)

**Nota: 7**

**Justificación:**  
_Hemos completado todos los objetivos del sprint con una app funcional, navegable y con un diseño cuidado. Restamos puntos porque invertimos tiempo en intentar abstraer componentes compartidos que finalmente no llegamos a implementar bien, y porque los errores de iconos nos costaron más de lo esperado. Es nuestro primer proyecto en Kotlin y Android y consideramos que el resultado es sólido dado el punto de partida._