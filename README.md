# LIBRIS — Android / 3 APIs integradas

**Proyecto de Programación 3 — Kotlin + Jetpack Compose + Retrofit + MVVM**.

Una biblioteca digital que busca libros, consulta información del autor y encuentra
versiones de lectura y descarga que realmente estén en Project Gutenberg.

## Funcionalidades

- Búsqueda en Google Books por título o autor, con respaldo de Open Library cuando Google Books falle. Resultados reales con portada,
  nombre, autor, fecha y editorial cuando existen.
- Ficha del libro con descripción, idioma, páginas, ISBN e información editorial.
- Open Library: búsqueda del autor principal, biografía y hasta ocho obras adicionales.
- Gutendex: búsqueda de versiones de Project Gutenberg; muestra lectura HTML, EPUB,
  texto o PDF **solo si** la API devuelve esos formatos y existe coincidencia fiable
  de título y autor. Abre el navegador o un lector compatible.
- Cargas independientes: si Open Library o Gutendex falla, Google Books sigue visible.
- Manejo de campos faltantes, conexiones fallidas y libros sin versión pública.
- Interfaz en español con tema editorial.

## Requisitos

- Android Studio (versión compatible con Android Gradle Plugin 8.7.3).
- JDK 17 o 21. En Android Studio: **Settings → Build, Execution, Deployment →
  Build Tools → Gradle → Gradle JDK**, usar `jbr-17` o la JBR disponible.
- SDK Android API **35** instalado (desde SDK Manager).
- Internet para descargar dependencias y consultar los servicios.
- Emulador o teléfono Android 8.0 (API 26) o superior.

## Ejecutar paso a paso (Windows)

1. Descomprime `Libris_3_APIs.zip`.
2. Abre Android Studio → **Open** → selecciona la carpeta `Libris_3_APIs`
   (la que contiene `settings.gradle.kts`).
3. Espera la sincronización de Gradle y acepta la instalación de SDK si es necesario.
4. Selecciona un emulador o conecta tu Android con depuración USB.
5. Presiona **Run ▶** para abrir `Libris`.

Los archivos `gradlew` y `gradlew.bat` incluyen un lanzador Java educativo
(`gradle/wrapper/gradle-wrapper.jar`, cuyo fuente está incluido). **No es el JAR
oficial del Gradle Wrapper**: descarga la distribución oficial Gradle 8.9
especificada en `gradle-wrapper.properties` al ejecutarlo por primera vez.
Necesita conexión a Internet inicial.

También puedes compilar desde PowerShell, dentro de la carpeta del proyecto:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat assembleDebug
```

El APK compilado quedará en `app\build\outputs\apk\debug\app-debug.apk`.

**Google Books se consulta primero sin clave** (los datos públicos no requieren autenticación de usuario), pero algunos entornos pueden recibir respuestas HTTP 403/429. La app mostrará el código real y buscará libros en Open Library como respaldo. La información del autor y las lecturas gratuitas siguen consultándose mediante Open Library y Gutendex.

Si Google Books devuelve HTTP 403 en tu emulador, puedes crear una clave habilitada para Google Books API en Google Cloud Console y colocarla en el archivo local.properties del proyecto (no en GitHub):

```properties
GOOGLE_BOOKS_API_KEY=TU_CLAVE_AQUI
```

Para API keys restringidas a aplicaciones Android, revisa cómo se identifican las peticiones REST hechas directamente por Retrofit; la restricción debe ser compatible con este flujo. No publiques claves reales en el repositorio ni en capturas. Reconstruye la app después de agregarla.


## Cómo usarla

1. La app abre con una búsqueda de demostración de `Don Quijote` realizada a Google Books.
2. Introduce título o autor y pulsa Buscar.
3. Toca una portada/ficha para abrir sus detalles.
4. La app consulta automáticamente Open Library y Gutendex para ese libro.
5. Pulsa otra obra del autor para iniciar una nueva búsqueda, o pulsa un enlace
   de Gutenberg para abrir la versión disponible.

## Estructura de Programación 3

Consulta [ARQUITECTURA.md](ARQUITECTURA.md) para el diagrama Mermaid y la tabla
con las capas **API, Repository, DTOs, Mapper, ViewModel, View** para las tres APIs.

## Consideraciones para la exposición

- **Integración real**: el libro seleccionado en Google Books proporciona el autor
  que se busca en Open Library y el título + autor que se validan en Gutendex.
- **No se mezclan temas**: todas las fuentes aportan un dato distinto del mismo libro.
- **DTO ≠ Entidad**: los DTOs reflejan el JSON; los mapeadores forman entidades
  limpias que consumen los ViewModels y las Views.
- **Tres ViewModels**: Books, Author y Reading; se muestran juntos en DetailScreen.
- **MVVM**: la vista no realiza llamadas HTTP; Retrofit se usa en los repositorios.
- **Resistencia a fallos**: una API sin registros no inventa información.
- **Licencias**: Gutendex informa el estado de dominio público en EE. UU.; hay que
  verificar si los contenidos son reutilizables en la jurisdicción del usuario.

## Fuentes

- Google Books: https://developers.google.com/books/docs/v1/using
- Open Library Authors API: https://openlibrary.org/dev/docs/api/authors
- Gutendex: https://github.com/garethbjohnson/gutendex

## Estado de validación

Se incluyen pruebas unitarias de `BookMatcher` en
`app/src/test/java/com/augusto/libris/domain/matching/BookMatcherTest.kt`.

Si no existe SDK Android o acceso a Maven/Google en el entorno donde se generó
el proyecto, la compilación completa no se puede comprobar allí. La primera
sincronización debe ejecutarse en Android Studio con conexión a Internet.

## Si las búsquedas fallan en el emulador

1. Abre Chrome **dentro del emulador** y carga `https://openlibrary.org` para comprobar que el emulador tenga red.
2. Pulsa **Buscar libros**. Ahora se muestra la causa técnica de Google Books (HTTP 403, HTTP 429, SSL, DNS, etc.) y se intenta una búsqueda de respaldo en Open Library.
3. Si aparece **HTTP 403** en Google Books y quieres que Google Books funcione como origen principal, configura la clave opcional en `local.properties` como se explica arriba.
4. Si ambas APIs fallan por **DNS o falta de internet**, arregla la conexión del emulador: activa Wi-Fi/datos virtuales, desactiva VPN problemática, o ejecuta **Cold Boot Now** desde Device Manager.
5. Si hay dudas, abre **Logcat** en Android Studio y filtra por `Libris/GoogleBooks` y `Libris/OpenLibrary` para inspeccionar la excepción de cada servicio.
