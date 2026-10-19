package com.augusto.libris.ui.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Log
import retrofit2.HttpException
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException
import androidx.lifecycle.viewModelScope
import com.augusto.libris.data.repository.GutenbergRepository
import com.augusto.libris.domain.model.Book
import com.augusto.libris.domain.model.FreeEdition
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReadingUiState(
    val loading: Boolean = false,
    val edition: FreeEdition? = null,
    val message: String? = null
)

class ReadingViewModel(private val repository: GutenbergRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingUiState())
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()
    private var request: Job? = null

    fun load(book: Book) {
        request?.cancel()
        request = viewModelScope.launch {
            _uiState.value = ReadingUiState(loading = true)
            try {
                val edition = repository.findEdition(book)
                _uiState.value = if (edition == null) {
                    ReadingUiState(message = "No se encontró una coincidencia confiable en Project Gutenberg para este libro.")
                } else {
                    ReadingUiState(edition = edition)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.e("Libris/Gutendex", "Falló la búsqueda de Gutenberg", error)
                val explanation = when (error) {
                    is HttpException -> "Gutendex devolvió HTTP ${error.code()}. " +
                        when (error.code()) {
                            403 -> "El servidor está rechazando la petición de la app (aunque Chrome pueda abrirlo)."
                            429 -> "El servidor alcanzó su límite de solicitudes; prueba más tarde."
                            in 500..599 -> "El servidor tiene un problema temporal."
                            else -> "No aceptó la consulta de la aplicación."
                        }
                    is UnknownHostException -> "No se puede resolver gutendex.com desde la aplicación (DNS/red)."
                    is SocketTimeoutException -> "Gutendex tardó demasiado en responder (timeout)."
                    is SSLException -> "Error SSL al conectar con Gutendex."
                    else -> "Error ${error.javaClass.simpleName}: ${error.message.orEmpty().take(100)}"
                }
                _uiState.value = ReadingUiState(message = explanation)
            }
        }
    }
}
