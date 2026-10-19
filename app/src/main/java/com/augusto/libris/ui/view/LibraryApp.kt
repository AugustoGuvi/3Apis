package com.augusto.libris.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.augusto.libris.LibraryApplication
import com.augusto.libris.di.LibraryViewModelFactory
import com.augusto.libris.domain.model.Book
import com.augusto.libris.ui.viewmodel.AuthorViewModel
import com.augusto.libris.ui.viewmodel.BooksViewModel
import com.augusto.libris.ui.viewmodel.ReadingViewModel

/** Vista contenedora: enlaza las tres capas ViewModel al seleccionar un solo libro. */
@Composable
fun LibraryApp() {
    val app = LocalContext.current.applicationContext as LibraryApplication
    val factory = remember(app) { LibraryViewModelFactory(app.container) }
    val booksVm: BooksViewModel = viewModel(factory = factory)
    val authorVm: AuthorViewModel = viewModel(factory = factory)
    val readingVm: ReadingViewModel = viewModel(factory = factory)

    val booksState by booksVm.uiState.collectAsState()
    val authorState by authorVm.uiState.collectAsState()
    val readingState by readingVm.uiState.collectAsState()
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    BackHandler(enabled = selectedBook != null) { selectedBook = null }
    LaunchedEffect(selectedBook?.id) {
        selectedBook?.let { book ->
            authorVm.load(book.authors.firstOrNull())
            readingVm.load(book)
        }
    }

    val activeBook = selectedBook
    if (activeBook == null) {
        SearchScreen(
            state = booksState,
            onSearch = booksVm::search,
            onBookClick = { selectedBook = it }
        )
    } else {
        DetailScreen(
            book = activeBook,
            authorState = authorState,
            readingState = readingState,
            onBack = { selectedBook = null },
            onWorkClick = { title ->
                selectedBook = null
                booksVm.search(title)
            }
        )
    }
}
