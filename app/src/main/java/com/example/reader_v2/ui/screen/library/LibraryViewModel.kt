package com.example.reader_v2.ui.screen.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val repository: BookRepository,
    ) : ViewModel() {
        val books: StateFlow<List<Book>> =
            repository.getAllBooks().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        fun addAndExtractBook(uri: Uri) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val bookId = repository.addAndExtractBook(uri)
                    println("Successfully added book with ID: $bookId")
                } catch (e: Exception) {
                    println("Error adding book: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            }
        }

        fun deleteBook(bookId: String) {
            viewModelScope.launch {
                repository.deleteBook(bookId)
            }
        }
    }
