package com.augusto.libris.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.augusto.libris.domain.model.Book
import com.augusto.libris.ui.components.BookResultCard
import com.augusto.libris.ui.components.LoadingPanel
import com.augusto.libris.ui.components.MessagePanel
import com.augusto.libris.ui.theme.*
import com.augusto.libris.ui.viewmodel.BooksUiState

private val suggestions = listOf("Don Quijote", "Orgullo y prejuicio", "Drácula", "El principito")

@Composable
fun SearchScreen(
    state: BooksUiState,
    onSearch: (String) -> Unit,
    onBookClick: (Book) -> Unit
) {
    var input by rememberSaveable { mutableStateOf("Don Quijote") }
    LaunchedEffect(state.currentQuery) {
        if (state.currentQuery.isNotBlank()) input = state.currentQuery
    }
    val keyboard = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Paper).safeDrawingPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 21.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Ink, shape = RoundedCornerShape(15.dp)) {
                    Icon(
                        Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = Honey,
                        modifier = Modifier.padding(11.dp).size(26.dp)
                    )
                }
                Spacer(Modifier.width(11.dp))
                Column {
                    Text("LIBRIS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("TU BIBLIOTECA DIGITAL", style = MaterialTheme.typography.labelSmall, color = SecondaryInk)
                }
            }
        }
        item {
            Surface(shape = RoundedCornerShape(28.dp), color = Ink, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(25.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "HISTORIAS POR DESCUBRIR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Honey
                    )
                    Text(
                        "Encuentra tu\npróxima lectura.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Busca un libro, conoce a su autor y descubre si existe una versión de lectura gratuita.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCFD8E4)
                    )
                    Text(
                        "GOOGLE BOOKS  ·  OPEN LIBRARY  ·  GUTENDEX",
                        style = MaterialTheme.typography.labelSmall,
                        color = Honey
                    )
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Text("Busca un libro o autor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    placeholder = { Text("Ej. Cien años de soledad") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Rust) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboard?.hide()
                        onSearch(input)
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Rust,
                        unfocusedBorderColor = Border
                    )
                )
                Button(
                    onClick = { keyboard?.hide(); onSearch(input) },
                    modifier = Modifier.fillMaxWidth().height(51.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rust)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Buscar libros", fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("EXPLORA TAMBIÉN", style = MaterialTheme.typography.labelSmall, color = SecondaryInk)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        AssistChip(
                            onClick = { input = suggestion; onSearch(suggestion) },
                            label = { Text(suggestion) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color.White),
                            border = null
                        )
                    }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Resultados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (!state.isLoading && state.error == null && state.currentQuery.isNotBlank()) {
                    Text("${state.books.size} mostrados", style = MaterialTheme.typography.labelMedium, color = SecondaryInk)
                }
            }
        }
        when {
            state.isLoading -> item { LoadingPanel("Buscando libros reales en Google Books…") }
            state.error != null -> item { MessagePanel(state.error) }
            state.books.isEmpty() -> item { MessagePanel("No encontramos resultados. Prueba con otro título o autor.") }
            else -> items(state.books, key = { it.id }) { book ->
                BookResultCard(book) { onBookClick(book) }
            }
        }
        item {
            Text(
                "Datos de Google Books. La disponibilidad de portadas, descripciones y ediciones depende de cada registro.",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryInk
            )
        }
    }
}
