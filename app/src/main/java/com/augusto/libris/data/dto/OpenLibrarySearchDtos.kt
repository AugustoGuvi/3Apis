package com.augusto.libris.data.dto

import com.google.gson.annotations.SerializedName

/** DTOs de búsqueda de obras: endpoint search.json (no confundir con la búsqueda de autores). */
data class OpenLibraryBooksResponseDto(
    val numFound: Int? = null,
    val docs: List<OpenLibraryBookDto>? = null
)

data class OpenLibraryBookDto(
    val key: String? = null,
    val title: String? = null,
    @SerializedName("author_name") val authorNames: List<String>? = null,
    @SerializedName("first_publish_year") val firstPublishYear: Int? = null,
    @SerializedName("cover_i") val coverId: Long? = null,
    val language: List<String>? = null,
    val isbn: List<String>? = null,
    val publisher: List<String>? = null,
    @SerializedName("number_of_pages_median") val pageCount: Int? = null
)
