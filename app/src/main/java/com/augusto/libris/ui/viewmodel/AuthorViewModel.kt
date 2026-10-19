package com.augusto.libris.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.augusto.libris.data.repository.AuthorRepository
import com.augusto.libris.domain.model.Author
import com.augusto.libris.domain.model.LiteraryWork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthorUiState(
    val loading: Boolean = false,
    val author: Author? = null,
    val works: List<LiteraryWork> = emptyList(),
    val message: String? = null
)

class AuthorViewModel(private val repository: AuthorRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthorUiState())
    val uiState: StateFlow<AuthorUiState> = _uiState.asStateFlow()
    private var request: Job? = null

    fun load(name: String?) {
        request?.cancel()
        if (name.isNullOrBlank()) {
            _uiState.value = AuthorUiState(message = "Esta ficha no incluye el nombre del autor.")
            return
        }
        request = viewModelScope.launch {
            _uiState.value = AuthorUiState(loading = true)
            try {
                val result = repository.getAuthorAndWorks(name)
                _uiState.value = if (result == null) {
                    AuthorUiState(message = "No se encontró un autor con coincidencia suficiente en Open Library.")
                } else {
                    AuthorUiState(author = result.first, works = result.second)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = AuthorUiState(
                    message = "Open Library no respondió. Puedes seguir consultando el libro."
                )
            }
        }
    }
}
