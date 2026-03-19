package com.example.reader_v2.ui.screen.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.model.Book
import com.example.reader_v2.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel @Inject constructor(private val repository: BookRepository): ViewModel() {
    val books: StateFlow<List<Book>> = repository.getAllBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addBook(uri: Uri) {
        viewModelScope.launch {
            repository.addBook(uri)
                .onSuccess { id ->
                    println("Added: $id")
                }
                .onFailure { e ->
                    println("Failed: ${e.message}")
                }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
        }
    }
}
