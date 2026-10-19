package com.augusto.libris.data.repository

import android.util.Log
import com.augusto.libris.data.mapper.toBook
import com.augusto.libris.data.remote.GoogleBooksApi
import com.augusto.libris.data.remote.OpenLibraryApi
import com.augusto.libris.domain.model.Book
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

enum class CatalogSource { GOOGLE_BOOKS, OPEN_LIBRARY }

data class BookSearchResult(
    val books: List<Book>,
    val total: Int,
    val source: CatalogSource,
    val notice: String? = null
)

/** Google Books siempre se consulta primero; Open Library es respaldo cuando no responde. */
class BookRepository(
    private val googleApi: GoogleBooksApi,
    private val openLibraryApi: OpenLibraryApi,
    private val googleBooksApiKey: String = ""
) {
    suspend fun search(query: String): BookSearchResult {
        try {
            val response = googleApi.searchBooks(
                query = query,
                apiKey = googleBooksApiKey.takeIf { it.isNotBlank() }
            )
            val books = response.items.orEmpty().mapNotNull { it.toBook() }.distinctBy { it.id }
            return BookSearchResult(
                books = books,
                total = response.totalItems ?: books.size,
                source = CatalogSource.GOOGLE_BOOKS
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (googleFailure: Exception) {
            Log.w("Libris/GoogleBooks", "Falló Google Books. Probando Open Library.", googleFailure)
            try {
                val response = openLibraryApi.searchBooks(query)
                val books = response.docs.orEmpty().mapNotNull { it.toBook() }.distinctBy { it.id }
                return BookSearchResult(
                    books = books,
                    total = response.numFound ?: books.size,
                    source = CatalogSource.OPEN_LIBRARY,
                    notice = "Google Books no respondió (${describeApiError(googleFailure)}). " +
                        "Mostrando resultados reales de Open Library."
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (openLibraryFailure: Exception) {
                Log.e("Libris/OpenLibrary", "Falló también la búsqueda alternativa.", openLibraryFailure)
                throw CatalogUnavailableException(
                    "Google Books: ${describeApiError(googleFailure)}. " +
                    "Open Library: ${describeApiError(openLibraryFailure)}. " +
                    "Abre Chrome en el emulador para verificar Internet e inténtalo otra vez.",
                    openLibraryFailure
                )
            }
        }
    }
}

class CatalogUnavailableException(message: String, cause: Throwable) : IOException(message, cause)

fun describeApiError(error: Throwable): String = when (error) {
    is HttpException -> when (error.code()) {
        401 -> "HTTP 401: acceso no autorizado"
        403 -> "HTTP 403: acceso restringido; puede requerirse clave de Google Books"
        429 -> "HTTP 429: límite de solicitudes excedido"
        in 500..599 -> "HTTP ${error.code()}: servidor temporalmente no disponible"
        else -> "HTTP ${error.code()}"
    }
    is UnknownHostException -> "sin acceso a Internet o problema de DNS"
    is SocketTimeoutException -> "se agotó el tiempo de espera"
    is SSLException -> "fallo de seguridad SSL/certificado (revisa fecha y hora del dispositivo)"
    is IOException -> "error de red: ${error.javaClass.simpleName}"
    else -> "respuesta inválida: ${error.javaClass.simpleName}"
}
