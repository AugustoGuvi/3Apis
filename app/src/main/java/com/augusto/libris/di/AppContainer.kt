package com.augusto.libris.di

import com.augusto.libris.data.remote.GoogleBooksApi
import com.augusto.libris.data.remote.GutendexApi
import com.augusto.libris.data.remote.OpenLibraryApi
import com.augusto.libris.data.repository.AuthorRepository
import com.augusto.libris.data.repository.BookRepository
import com.augusto.libris.data.repository.GutenbergRepository
import com.augusto.libris.ui.viewmodel.AuthorViewModel
import com.augusto.libris.ui.viewmodel.BooksViewModel
import com.augusto.libris.ui.viewmodel.ReadingViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Inyección de dependencias simple para no exigir Hilt ni configuración adicional. */
class AppContainer {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private fun retrofit(baseUrl: String) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val bookRepository = BookRepository(
        retrofit("https://www.googleapis.com/books/v1/").create(GoogleBooksApi::class.java)
    )
    val authorRepository = AuthorRepository(
        retrofit("https://openlibrary.org/").create(OpenLibraryApi::class.java)
    )
    val gutenbergRepository = GutenbergRepository(
        retrofit("https://gutendex.com/").create(GutendexApi::class.java)
    )
}

class LibraryViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(BooksViewModel::class.java) ->
            BooksViewModel(container.bookRepository) as T
        modelClass.isAssignableFrom(AuthorViewModel::class.java) ->
            AuthorViewModel(container.authorRepository) as T
        modelClass.isAssignableFrom(ReadingViewModel::class.java) ->
            ReadingViewModel(container.gutenbergRepository) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
