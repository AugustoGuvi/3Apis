package com.augusto.libris.data.dto

/** DTO: representación exclusiva del JSON devuelto por Google Books. */
data class GoogleBooksResponseDto(
    val totalItems: Int? = null,
    val items: List<GoogleVolumeDto>? = null
)

data class GoogleVolumeDto(
    val id: String? = null,
    val volumeInfo: GoogleVolumeInfoDto? = null
)

data class GoogleVolumeInfoDto(
    val title: String? = null,
    val subtitle: String? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val imageLinks: GoogleImageLinksDto? = null,
    val publisher: String? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val language: String? = null,
    val categories: List<String>? = null,
    val industryIdentifiers: List<GoogleIdentifierDto>? = null,
    val infoLink: String? = null
)

data class GoogleImageLinksDto(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

data class GoogleIdentifierDto(
    val type: String? = null,
    val identifier: String? = null
)
