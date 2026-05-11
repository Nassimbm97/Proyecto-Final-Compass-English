# 📘 CompassEnglish

> Aplicación Android de aprendizaje de vocabulario en inglés con algoritmo de repetición espaciada **FSRS-4.5**.

---

## 🧰 Requisitos previos

| Herramienta | Versión recomendada |
|---|---|
| Docker Desktop | Última versión |
| IntelliJ IDEA | 2023+ |
| Android Studio | Hedgehog o superior |
| OpenJDK | 21 |
| XAMPP / MySQL | Última versión |

---

## 🐳 Opción 1 — Docker *(Recomendada)*

Levanta la base de datos MySQL y el backend Spring Boot automáticamente, sin instalar nada extra.

### 📁 Estructura del proyecto

```
CompassEnglish_backend/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/
└── application-local-docker.properties
```

### 🚀 Levantar todo con un comando

Abre una terminal en `CompassEnglish_backend/` y ejecuta:

```bash
docker compose up --build
```

Docker se encarga automáticamente de:

- Levantar **MySQL** y crear la base de datos `CompassEnglishBD`
- Construir y ejecutar el **backend Spring Boot**
- Exponer la API en `http://localhost:8080`

### 🧠 Confirmación de éxito

```
Started CompassEnglishApplication in X seconds
```

### 📱 Configurar la URL en Android

**Emulador:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

**Móvil físico:**
```kotlin
private const val BASE_URL = "http://TU_IP:8080/"
```

### 🧯 Parar Docker

```bash
# Pausar (mantiene datos)
docker compose stop

# Detener y eliminar contenedores
docker compose down

# Detener, eliminar contenedores Y volúmenes (borra datos)
docker compose down -v
```

---

## 💻 Opción 2 — Emulador Android en PC (XAMPP + IntelliJ)

Todo corre en la misma máquina: base de datos local, backend y emulador Android.

### 🛢️ Paso 1 — Levantar la base de datos con XAMPP

1. Abre **XAMPP** y arranca el módulo **MySQL**
2. Abre **MySQL Workbench** (o phpMyAdmin)
3. Ejecuta el script `CompassEnglishBD.sql` para crear e inicializar la base de datos

### ⚙️ Paso 2 — Correr el backend con IntelliJ

1. Abre el proyecto en **IntelliJ IDEA** y espera a que Maven descargue las dependencias
2. Asegúrate de que `application.properties` apunta a tu MySQL local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/compassenglish
spring.datasource.username=root
spring.datasource.password=
server.port=8080
server.address=0.0.0.0
```

3. Ejecuta `CompassEnglishApplication.java`

> El puerto `8080` es el que usa el emulador de Android por defecto para comunicarse con el host.

### 📱 Paso 3 — Configurar y lanzar Android Studio

Establece la URL base en el proyecto Android:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

Luego, en Android Studio:

1. Ve a **Device Manager** y selecciona un dispositivo (ej. Pixel 6)
2. Pulsa **Run ▶️**

---

## 📱 Opción 3 — Móvil físico (misma red WiFi)

Ideal para probar la app en un dispositivo real conectado por USB.

### 🌐 Paso 1 — Obtener la IP del PC

**Windows:**
```bash
ipconfig
```

**Linux / macOS:**
```bash
ip addr
```

Ejemplo de IP resultante: `192.168.1.33`

### ⚙️ Paso 2 — Configurar Android

```kotlin
private const val BASE_URL = "http://192.168.1.33:8080/"
```

### 🔥 Paso 3 — Abrir el firewall

Permite el tráfico **TCP en el puerto 8080** en el firewall de tu sistema operativo.

### 📲 Paso 4 — Activar modo desarrollador en el móvil

1. Ve a **Ajustes → Acerca del teléfono**
2. Toca **"Número de compilación"** 7 veces
3. Activa **Depuración USB** en las opciones de desarrollador
4. Conecta el móvil al PC por USB

### ▶️ Paso 5 — Ejecutar la app

1. En Android Studio, selecciona tu dispositivo físico en el menú desplegable
2. Pulsa **Run ▶️**

---

## 🧯 Solución de problemas

| Problema | Solución |
|---|---|
| Backend no inicia | Revisar logs de Docker o la consola de IntelliJ |
| No conecta a la base de datos | Reiniciar contenedores o verificar que XAMPP MySQL está activo |
| Puerto 8080 ocupado | Cambiarlo en `application.properties` y en `BASE_URL` |
| Móvil no conecta a la API | Verificar IP, que estén en la misma red WiFi y que el firewall permita el puerto |
| Datos no cargan | Ejecutar `docker compose down -v` y volver a levantar |
