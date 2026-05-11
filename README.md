📘 CompassEnglish — Guía de configuración y ejecución

Aplicación Android de aprendizaje de vocabulario en inglés con algoritmo de repetición espaciada FSRS-4.5.

🧰 Requisitos previos
Herramienta	Versión recomendada
Docker Desktop	Última versión
IntelliJ IDEA	2023+
Android Studio	Hedgehog o superior
OpenJDK	21
🐳 Opción 1 — Docker (RECOMENDADA)

Con Docker puedes levantar la base de datos MySQL y el backend Spring Boot automáticamente sin instalar nada extra.

📁 Estructura
CompassEnglish_backend/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/
└── application-local-docker.properties
🚀 Paso único — Levantar todo

Abre una terminal en CompassEnglish_backend/:

docker compose up --build
⚙️ Qué hace Docker automáticamente
Levanta MySQL
Crea la base de datos: CompassEnglishBD
Construye el backend Spring Boot
Expone la API en:
http://localhost:8080
🧠 Confirmación de éxito
Started CompassEnglishApplication in X seconds
📱 Configuración Android

Emulador:

private const val BASE_URL = "http://10.0.2.2:8080/"

Móvil físico:

private const val BASE_URL = "http://TU_IP:8080/"
🧯 Parar Docker
docker compose stop
docker compose down
docker compose down -v
💻 Opción 2 — Emulador Android en PC

Todo corre en la misma máquina: base de datos, backend y emulador Android.

🧱 Paso 1 — Configurar backend
Abrir proyecto en IntelliJ IDEA
Esperar Maven
Ejecutar CompassEnglishApplication.java
spring.datasource.url=jdbc:mysql://localhost:3306/compassenglish
spring.datasource.username=root
spring.datasource.password=
server.port=8080
server.address=0.0.0.0
📱 Paso 2 — Configurar Android
private const val BASE_URL = "http://10.0.2.2:8080/"
▶️ Paso 3 — Ejecutar emulador
Device Manager → Pixel 6
Run ▶️
📱 Opción 3 — Móvil físico (misma red WiFi)
🌐 Paso 1 — Obtener IP del PC

Windows:

ipconfig

Linux/Mac:

ip addr

Ejemplo:

192.168.1.33
⚙️ Paso 2 — Configurar Android
private const val BASE_URL = "http://192.168.1.33:8080/"
🔥 Firewall

Permitir puerto 8080 TCP en el sistema.

📲 Paso 3 — Activar modo desarrollador
7 toques en “Número de compilación”
Activar depuración USB
Conectar móvil al PC
▶️ Paso 4 — Ejecutar app
Seleccionar dispositivo en Android Studio
Run ▶️
🧯 Solución de problemas
Problema	Solución
Backend no inicia	Revisar logs de Docker o IntelliJ
No conecta a BD	Reiniciar contenedores
Puerto ocupado	Cambiar 8080
Móvil no conecta	Revisar IP y firewall
Datos no cargan	docker compose down -v