package com.augusto.libris.domain.model

/** Clases de entidad (modelo de dominio), separadas de las respuestas de las APIs. */
data class Book(
    val id: String,
    val title: String,
    val subtitle: String?,
    val authors: List<String>,
    val descriptionHtml: String?,
    val coverUrl: String?,
    val publisher: String?,
    val publishedDate: String?,
    val pageCount: Int?,
    val language: String?,
    val categories: List<String>,
    val isbn: String?,
    val infoUrl: String?
)

data class Author(
    val id: String,
    val name: String,
    val bio: String?,
    val birthDate: String?,
    val deathDate: String?,
    val photoUrl: String?
)

data class LiteraryWork(val id: String, val title: String)

data class ReadingLink(val label: String, val url: String)

data class FreeEdition(
    val id: Int,
    val title: String,
    val authors: List<String>,
    val languages: List<String>,
    val links: List<ReadingLink>,
    val catalogUrl: String,
    val downloadCount: Int
)
