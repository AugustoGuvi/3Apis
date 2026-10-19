package com.augusto.libris.data.mapper

import com.augusto.libris.data.dto.GutendexBookDto
import com.augusto.libris.domain.model.FreeEdition
import com.augusto.libris.domain.model.ReadingLink

/** Solo se presentan formatos de lectura reconocidos y URLs HTTP(S). */
fun GutendexBookDto.toFreeEdition(): FreeEdition {
    val availableFormats = this.formats.orEmpty()
    fun formatUrl(match: (String) -> Boolean): String? = availableFormats.entries
        .firstOrNull { (mime, url) -> match(mime.lowercase()) && url.startsWith("https://") }
        ?.value
        ?: availableFormats.entries.firstOrNull { (mime, url) ->
            match(mime.lowercase()) && url.startsWith("http://")
        }?.value?.replaceFirst("http://", "https://")

    val links = listOfNotNull(
        formatUrl { it.startsWith("text/html") }?.let { ReadingLink("Leer online (HTML)", it) },
        formatUrl { it.startsWith("application/epub+zip") }?.let { ReadingLink("Descargar EPUB", it) },
        formatUrl { it.startsWith("text/plain") }?.let { ReadingLink("Descargar texto", it) },
        formatUrl { it.startsWith("application/pdf") }?.let { ReadingLink("Descargar PDF", it) }
    )

    return FreeEdition(
        id = id,
        title = title.orEmpty(),
        authors = authors.orEmpty().mapNotNull { it.name },
        languages = languages.orEmpty(),
        links = links,
        catalogUrl = "https://www.gutenberg.org/ebooks/$id",
        downloadCount = downloadCount
    )
}
