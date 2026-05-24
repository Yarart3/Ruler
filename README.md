# 🧭 Ruler

> *Los mejores viajes no se improvisan, se miden.*

Bienvenido a **Ruler**, la app para planificar tus viajes sin volverte loco en el intento. Olvídate de las notas en papel y las decenas de pestañas abiertas en el navegador. con Ruler tienes todo lo que necesitas en un solo lugar, desde el itinerario hasta los lugares que no puedes perderte.

---

## 📖 Sobre el Proyecto

Ruler nació como proyecto universitario con una idea bastante clara: hacer una app de viajes que de verdad apeteciera usar. Queríamos algo limpio, fácil de entender y que ayudara de verdad a organizar un viaje sin complicarlo más de lo necesario.

Está construida con **Kotlin + Jetpack Compose**, lo que nos permite tener una interfaz moderna y fluida. Es nuestro primer proyecto en Android, así que hemos aprendido bastante por el camino.

---

## 🔥 Características Principales

✅ **Planificación de itinerarios** — Crea y organiza los días de tu viaje de forma visual.  
✅ **Gestión de viajes** — Guarda varios viajes y accede a ellos cuando quieras.  
✅ **Reservas de hotel** — Busca hoteles por ciudad y fechas, consulta habitaciones y completa reservas.  
✅ **Galería por viaje** — Guarda fotos de cada viaje y revísalas en una galería propia.  
✅ **Persistencia local y remota** — Combina Room para datos locales y Retrofit para la API de hoteles.  
✅ **Preferencias de usuario** — Personaliza la app a tu gusto.  
✅ **Soporte multiidioma** — Disponible en varios idiomas.   

---

## 📲 Tecnologías Usadas

- **Kotlin** — Lenguaje principal de desarrollo.
- **Jetpack Compose** — Para construir la UI de forma moderna y reactiva.
- **Navigation Component** — Para gestionar la navegación entre pantallas.
- **Room** — Persistencia local de viajes, reservas, usuarios e imágenes.
- **Hilt** — Inyección de dependencias.
- **Retrofit + OkHttp** — Integración con la API REST de hoteles.
- **Firebase Authentication** — Login, registro y gestión de sesión.
- **Android Oreo (API 26+)** — Versión mínima elegida por su amplia compatibilidad con dispositivos actuales sin renunciar a funcionalidades modernas.

---

## 🧱 Arquitectura

Ruler sigue una arquitectura **MVVM** con separación en capas:

- `ui/screens` para las pantallas
- `ui/viewmodels` para la lógica de presentación
- `domain` para modelos e interfaces
- `data` para Room, Retrofit y repositorios
- `di` para la configuración de dependencias con Hilt

En Sprint 04 esta arquitectura se amplió con:

- API remota de hoteles
- reservas persistidas localmente
- galería multimedia por viaje

La documentación detallada de arquitectura y base de datos está en [docs/design.md](docs/design.md).

---

## 👥 Equipo

- Gerard Guarro Pérez
- Nel Banqué Torné

---

## 📄 Licencia

Este proyecto está bajo la licencia **MIT**. 
