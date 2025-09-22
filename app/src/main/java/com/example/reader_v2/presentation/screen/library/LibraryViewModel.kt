package com.example.reader_v2.presentation.screen.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.domain.AddAndExtractBookUseCase
import com.example.reader_v2.domain.utils.Outcome
import com.example.reader_v2.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val addAndExtractBookUseCase: AddAndExtractBookUseCase,
        private val repository: BookRepository,
    ) : ViewModel() {
        val books: StateFlow<List<Book>> =
            repository.getAllBooks().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        fun addAndExtractBook(uri: Uri) {
            viewModelScope.launch {
                when (val result = addAndExtractBookUseCase(uri)) {
                    is Outcome.Success -> {
                        val bookId = result.data
                        println("Successfully added book with ID: $bookId")
                    }
                    is Outcome.Error -> {
                        val error = result.error
                        println("Error adding book: ${error.message}")
                    }
                }
            }
        }
    }
