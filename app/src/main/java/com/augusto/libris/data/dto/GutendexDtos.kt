package com.augusto.libris.data.dto

import com.google.gson.annotations.SerializedName

/** DTO: formatos tiene claves MIME dinámicas: text/html, application/epub+zip, etc. */
data class GutendexResponseDto(val results: List<GutendexBookDto>? = null)

data class GutendexBookDto(
    val id: Int = 0,
    val title: String? = null,
    val authors: List<GutendexAuthorDto>? = null,
    val languages: List<String>? = null,
    val copyright: Boolean? = null,
    val formats: Map<String, String>? = null,
    @SerializedName("download_count") val downloadCount: Int = 0
)

data class GutendexAuthorDto(val name: String? = null)
