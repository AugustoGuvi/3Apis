package com.augusto.libris.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augusto.libris.data.repository.BookRepository
import com.augusto.libris.data.repository.CatalogSource
import com.augusto.libris.domain.model.Book
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BooksUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val totalItems: Int = 0,
    val currentQuery: String = "",
    val source: CatalogSource? = null,
    val notice: String? = null,
    val error: String? = null
)

class BooksViewModel(private val repository: BookRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()
    private var request: Job? = null

    init { search("Don Quijote") }

    fun search(rawQuery: String) {
        val query = rawQuery.trim()
        if (query.isEmpty()) {
            _uiState.value = BooksUiState(error = "Escribe un título o un autor para buscar.")
            return
        }
        request?.cancel()
        request = viewModelScope.launch {
            _uiState.value = BooksUiState(isLoading = true, currentQuery = query)
            try {
                val result = repository.search(query)
                _uiState.value = BooksUiState(
                    books = result.books,
                    totalItems = result.total,
                    currentQuery = query,
                    source = result.source,
                    notice = result.notice
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = BooksUiState(
                    currentQuery = query,
                    error = error.message ?: "No fue posible consultar los catálogos: ${error.javaClass.simpleName}."
                )
            }
        }
    }
}
