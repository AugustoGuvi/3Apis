package com.augusto.libris.data.repository

import kotlinx.coroutines.CancellationException
import com.augusto.libris.data.mapper.toAuthor
import com.augusto.libris.data.mapper.toWork
import com.augusto.libris.data.remote.OpenLibraryApi
import com.augusto.libris.domain.matching.BookMatcher
import com.augusto.libris.domain.model.Author
import com.augusto.libris.domain.model.LiteraryWork

/** Busca el autor por nombre y después reutiliza su identificador de Open Library. */
class AuthorRepository(private val api: OpenLibraryApi) {
    suspend fun getAuthorAndWorks(query: String): Pair<Author, List<LiteraryWork>>? {
        val hits = api.searchAuthors(query).docs.orEmpty()
        val match = hits
            .filter { it.key != null && it.name != null }
            .map { it to BookMatcher.authorSimilarity(query, it.name.orEmpty()) }
            .filter { (_, score) -> score >= 0.62 }
            .maxByOrNull { (_, score) -> score }?.first ?: return null
        val id = match.key.orEmpty().substringAfterLast('/')
        if (!Regex("OL[0-9]+A").matches(id)) return null

        val author = api.getAuthor(id).toAuthor(id, match.name ?: query)
        val works = try {
            api.getWorks(id).entries.orEmpty()
                .mapNotNull { it.toWork() }
                .distinctBy { it.title.lowercase() }
                .take(8)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Si falla la lista de obras, mantenemos la biografía ya consultada.
            emptyList()
        }
        return author to works
    }
}
