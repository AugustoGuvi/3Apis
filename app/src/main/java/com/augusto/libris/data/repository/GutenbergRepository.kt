package com.augusto.libris.data.repository

import com.augusto.libris.data.mapper.toFreeEdition
import com.augusto.libris.data.remote.GutendexApi
import com.augusto.libris.domain.matching.BookMatcher
import com.augusto.libris.domain.model.Book
import com.augusto.libris.domain.model.FreeEdition

class GutenbergRepository(private val api: GutendexApi) {
    suspend fun findEdition(book: Book): FreeEdition? {
        fun choose(candidates: List<com.augusto.libris.data.dto.GutendexBookDto>) =
            candidates.mapNotNull { dto ->
                if (dto.copyright != false) return@mapNotNull null
                val score = BookMatcher.matchScore(
                    selectedTitle = book.title,
                    selectedAuthors = book.authors,
                    candidateTitle = dto.title.orEmpty(),
                    candidateAuthors = dto.authors.orEmpty().mapNotNull { it.name }
                ) ?: return@mapNotNull null
                dto to score
            }.maxByOrNull { it.second }?.first

        val initial = api.searchBooks(book.title).results.orEmpty()
        var best = choose(initial)
        // Un reintento con un título corto ayuda cuando Google Books incluye subtítulos.
        if (best == null) {
            val shorter = BookMatcher.tokens(book.title).take(3).joinToString(" ")
            if (shorter.isNotBlank() && shorter != book.title.lowercase()) {
                best = choose(api.searchBooks(shorter).results.orEmpty())
            }
        }
        return best?.toFreeEdition()
    }
}
