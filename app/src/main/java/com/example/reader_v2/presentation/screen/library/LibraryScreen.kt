package com.example.reader_v2.presentation.screen.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader_v2.domain.model.Book
import com.example.reader_v2.presentation.component.BookCard

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    libViewModel: LibraryViewModel = hiltViewModel(),
) {
    val books: List<Book> by libViewModel.books.collectAsState(
        initial = emptyList(),
    )
    val filePickerScreenLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri ->
                uri?.let {
                    libViewModel.addAndExtractBook(it)
                }
            },
        )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    filePickerScreenLauncher.launch(
                        arrayOf("application/epub+zip"),
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                )
            }
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        if (books.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Your library is empty")
                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = { filePickerScreenLauncher.launch(arrayOf("application/epub+zip")) },
                ) {
                    Text(text = "Select EPUB File")
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.padding(innerPadding),
                columns = GridCells.Adaptive(minSize = 128.dp),
            ) {
                items(
                    items = books,
                    key = { it.id },
                ) { book ->
                    BookCard(
                        book = book,
                    )
                }
            }
        }
    }
}
