package com.augusto.libris.data.repository

import com.augusto.libris.data.mapper.toBook
import com.augusto.libris.data.remote.GoogleBooksApi
import com.augusto.libris.domain.model.Book

class BookRepository(private val api: GoogleBooksApi) {
    suspend fun search(query: String): Pair<List<Book>, Int> {
        val response = api.searchBooks(query)
        val books = response.items.orEmpty().mapNotNull { it.toBook() }
        return books to (response.totalItems ?: books.size)
    }
}
