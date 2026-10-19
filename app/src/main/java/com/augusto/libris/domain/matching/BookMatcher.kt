package com.augusto.libris.domain.matching

import java.text.Normalizer
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * Matching conservador entre catálogos distintos.
 * El ID de Google Books NO es un ID de Gutenberg ni de Open Library.
 * Coincidir por nombre no garantiza que sea la misma edición física.
 */
object BookMatcher {
    private val stopWords = setOf(
        "a", "an", "and", "by", "the", "of", "in", "on", "for", "with", "to",
        "de", "del", "la", "las", "el", "los", "un", "una", "y", "en", "por", "con"
    )

    fun tokens(text: String): Set<String> {
        val normalized = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^a-z0-9 ]"), " ")
        val meaningful = normalized.split(Regex("\\s+"))
            .filter { it.isNotBlank() && it !in stopWords }
        return meaningful.toSet()
    }

    fun similarity(a: String, b: String): Double {
        val left = tokens(a)
        val right = tokens(b)
        if (left.isEmpty() || right.isEmpty()) return 0.0
        val overlap = left.intersect(right).size.toDouble()
        val coverage = overlap / min(left.size, right.size).toDouble()
        val jaccardLike = overlap / max(left.size, right.size).toDouble()
        return 0.65 * coverage + 0.35 * jaccardLike
    }

    /**
     * Evita confundir autores que comparten solo un nombre o apellido.
     * Acepta una coincidencia de una palabra únicamente si ese es el nombre
     * más corto disponible (ej. "Cervantes" vs "Miguel de Cervantes").
     */
    fun authorSimilarity(a: String, b: String): Double {
        val left = tokens(a)
        val right = tokens(b)
        val common = left.intersect(right)
        if (common.isEmpty()) return 0.0
        if (min(left.size, right.size) >= 2 && common.size < 2) return 0.0
        if (min(left.size, right.size) == 1 && (common.singleOrNull()?.length ?: 0) < 4) return 0.0
        return similarity(a, b)
    }

    /** Retorna null para coincidencias insuficientes o potencialmente incorrectas. */
    fun matchScore(
        selectedTitle: String,
        selectedAuthors: List<String>,
        candidateTitle: String,
        candidateAuthors: List<String>
    ): Double? {
        val titleScore = similarity(selectedTitle, candidateTitle)
        if (titleScore < 0.80) return null
        if (selectedAuthors.isEmpty() || candidateAuthors.isEmpty()) {
            // Sin autor verificable, exigir una coincidencia de título muy fuerte.
            return if (titleScore >= 0.98) titleScore else null
        }
        val authorScore = selectedAuthors.maxOf { selected ->
            candidateAuthors.maxOf { candidate -> authorSimilarity(selected, candidate) }
        }
        if (authorScore < 0.48) return null
        return (titleScore * 0.72) + (authorScore * 0.28)
    }
}
