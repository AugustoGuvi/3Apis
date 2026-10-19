# Guía breve de exposición — Programación 3

## ¿Cuál es el problema que resuelve Libris?

Los libros, sus autores y las copias de lectura gratuita se encuentran en sitios
diferentes. Libris los presenta juntos para que el usuario investigue un libro sin
buscar manualmente en tres páginas.

## ¿Por qué son tres APIs armónicas?

**Un mismo libro desencadena las tres consultas**: Google Books lo encuentra,
Open Library describe a su autor y sus otras obras, y Gutendex permite acceder
al texto si Project Gutenberg tiene una edición de dominio público que coincida.
No se mezclan datos del clima, autos u otros temas.

## Guion para el profesor

«Nuestra aplicación se llama Libris y es una biblioteca digital Android. Está
implementada en Kotlin, Jetpack Compose, con arquitectura MVVM y Retrofit.
Usamos tres APIs: Google Books para buscar libros y sus ediciones; Open Library
para la biografía del autor y más obras; y Gutendex para consultar si hay
versiones de Project Gutenberg. Cada integración tiene su API service, DTOs,
Mapper, Repository, ViewModel y una sección visible. La pantalla de detalle
coordina las tres usando la entidad Book. Para no ofrecer un libro incorrecto,
comparamos el título y el autor antes de mostrar enlaces de lectura. Si una API
falla o no tiene datos, no inventamos resultados y las otras secciones siguen
funcionando».

## Demostración sugerida

1. Abrir Libris conectado a Internet.
2. Buscar **Pride and Prejudice** (la obra original suele encontrarse en Gutenberg).
3. Seleccionar una ficha de Jane Austen y señalar portada, descripción y edición.
4. Señalar biografía y otras obras desde Open Library.
5. Si el catálogo devuelve la edición, abrir «Leer online (HTML)» o «EPUB».
6. Buscar un libro reciente para mostrar que la app puede informar que no existe
   una versión gratuita fiable, en vez de fabricar un enlace.

La disponibilidad exacta depende de las bases de datos y del título de edición.
