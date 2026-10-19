package com.augusto.libris.data.mapper

import com.augusto.libris.data.dto.AuthorDetailDto
import com.augusto.libris.data.dto.AuthorWorkDto
import com.augusto.libris.domain.model.Author
import com.augusto.libris.domain.model.LiteraryWork

fun AuthorDetailDto.toAuthor(id: String, fallbackName: String): Author {
    val textBio = when {
        bio == null || bio.isJsonNull -> null
        bio.isJsonPrimitive -> bio.asString
        bio.isJsonObject -> bio.asJsonObject.get("value")
            ?.takeIf { it.isJsonPrimitive }?.asString
        else -> null
    }
    return Author(
        id = id,
        name = name?.takeIf { it.isNotBlank() } ?: fallbackName,
        bio = textBio?.takeIf { it.isNotBlank() },
        birthDate = birthDate,
        deathDate = deathDate,
        photoUrl = "https://covers.openlibrary.org/a/olid/$id-M.jpg?default=false"
    )
}

fun AuthorWorkDto.toWork(): LiteraryWork? =
    title?.takeIf { it.isNotBlank() }?.let {
        LiteraryWork(key.orEmpty().ifBlank { it }, it)
    }
