package com.augusto.libris.data.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/** DTO: la biografía de Open Library puede ser una cadena o un objeto { value: ... }. */
data class AuthorSearchResponseDto(val docs: List<AuthorSearchHitDto>? = null)

data class AuthorSearchHitDto(
    val key: String? = null,
    val name: String? = null,
    @SerializedName("birth_date") val birthDate: String? = null
)

data class AuthorDetailDto(
    val name: String? = null,
    val bio: JsonElement? = null,
    @SerializedName("birth_date") val birthDate: String? = null,
    @SerializedName("death_date") val deathDate: String? = null
)

data class AuthorWorksResponseDto(val entries: List<AuthorWorkDto>? = null)

data class AuthorWorkDto(
    val key: String? = null,
    val title: String? = null
)
