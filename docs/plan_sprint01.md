# Sprint 01 – Planning Document

## 1. Sprint Goal

En este primer sprint queremos montar toda la estructura inicial del proyecto para poder empezar a trabajar en condiciones. Básicamente, configurar Android Studio, crear el repositorio en GitHub y tener las pantallas principales de la app funcionando con navegación entre ellas, aunque sea con datos inventados.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|-------|-------------|----------------|-----------|
| T1.1 | Crear nombre del producto | Nel | 0.25 | Muy alta |
| T1.2 | Generar logo con IA | Nel | 0.25 | Muy Alta |
| T1.3 | Definir la versión de Android | Gerard | 0.25 | Alta |
| T1.4 | Seleccionar y configurar la versión de Kotlin | Gerard | 0.25 | Alta |
| T1.5 | Inicializar el proyecto en Android Studio | Gerard | 1 | Muy alta |
| T2.1 | Crear repositorio público en GitHub | Gerard | 0.5 | Muy alta |
| T2.2 | Inicializar repositorio Git local | Gerard | 0.25 | Muy alta |
| T2.3 | Añadir archivo LICENSE | Gerard | 0.25 | Baja |
| T2.4 | Redactar `CONTRIBUTING.md` con la estrategia de ramas | Gerard | 0.5 | Media |
| T2.5 | Crear `README.md` con descripción del proyecto e info del equipo | Nel | 0.5 | Alta |
| T2.6 | Crear carpeta `/docs` con la estructura de ficheros requerida | Gerard | 0.25 | Media |
| T2.7 | Redactar `docs/design.md` con las decisiones arquitectónicas | Gerard | 1 | Alta |
| T2.8 | Configurar ramas remotas y locales requeridas | Gerard | 0.5 | Alta |
| T2.9 | Hacer push del código inicial a GitHub (v0.1.0) | Gerard | 0.25 | Muy alta |
| T2.10 | Crear primera release en GitHub (v0.1.0) | Gerard | 0.25 | Muy alta |
| T3.1 | Desarrollar los core screen layouts con datos mock | Gerard | 4 | Muy alta |
| T3.2 | Implementar la navegación entre pantallas principales | Nel | 2 | Muy alta |
| T3.3 | Documentar el diagrama del modelo de datos en `design.md` | Gerard | 1 | Media |
| T3.4 | Implementar las clases del modelo de datos con funciones y `@TODO` | Gerard | 1.5 | Media |
| T4.1 | Crear Splash Screen con el logo de la app | Nel | 0.5 | Media |
| T4.2 | Implementar About Page con info del equipo, versión y resumen | Nel | 0.75 | Baja |
| T4.3 | Añadir pantalla de Terms & Conditions | Nel | 0.5 | Baja |
| T4.4 | Añadir pantalla de Preferencias (mock-only) con selección de idiomas | Nel | 0.75 | Baja |

---

## 3. Definition of Done (DoD)

- [ ] El proyecto compila y abre sin errores en Android Studio
- [ ] El repositorio está en GitHub, es público y sigue la estructura de carpetas del enunciado, con la release v1.0.0 publicada
- [ ] Todas las pantallas principales están creadas y tienen datos de ejemplo para que no queden vacías
- [ ] Se puede navegar entre pantallas sin que la app falle
- [ ] El modelo de datos está documentado en `docs/design.md` y las clases están implementadas en `domain/` con `@TODO` donde toque
- [ ] Las pantallas de Splash, About, Terms & Conditions y Preferencias existen, aunque sean solo visuales
- [ ] Todos los archivos de documentación (`README.md`, `CONTRIBUTING.md`, `design.md`, etc.) están rellenos y tienen sentido

---

## 4. Riesgos identificados

* **Kotlin desde cero:** nunca lo hemos usado, así que algunas cosas nos costarán más tiempo del que parece.
* **Android Studio es nuevo para nosotros:** configurarlo y entender cómo funciona puede comerse horas al principio.
* **Git con ramas y releases:** el flujo de trabajo con ramas no lo tenemos muy practicado y es fácil liarla.
* **Las estimaciones se nos van a quedar cortas:** es la primera vez que hacemos algo así y seguramente hemos calculado mal.



