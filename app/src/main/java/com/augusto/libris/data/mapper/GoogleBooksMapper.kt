package com.augusto.libris.data.mapper

import com.augusto.libris.data.dto.GoogleVolumeDto
import com.augusto.libris.domain.model.Book

/** Mapper: DTO externo -> Entidad Book interna. */
fun GoogleVolumeDto.toBook(): Book? {
    val info = volumeInfo ?: return null
    val validId = id?.takeIf { it.isNotBlank() } ?: return null
    val validTitle = info.title?.takeIf { it.isNotBlank() } ?: return null
    val cover = (info.imageLinks?.thumbnail ?: info.imageLinks?.smallThumbnail)
        ?.replace("http://", "https://")

    return Book(
        id = validId,
        title = validTitle,
        subtitle = info.subtitle,
        authors = info.authors.orEmpty().filter { it.isNotBlank() },
        descriptionHtml = info.description,
        coverUrl = cover,
        publisher = info.publisher,
        publishedDate = info.publishedDate,
        pageCount = info.pageCount,
        language = info.language,
        categories = info.categories.orEmpty(),
        isbn = info.industryIdentifiers.orEmpty()
            .firstOrNull { it.type == "ISBN_13" }?.identifier
            ?: info.industryIdentifiers.orEmpty().firstOrNull { it.type == "ISBN_10" }?.identifier,
        infoUrl = info.infoLink?.replace("http://", "https://")
    )
}
