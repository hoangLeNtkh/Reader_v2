package com.example.reader_v2.presentation.screen.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.domain.model.Book
import com.example.reader_v2.domain.model.SimpleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ReaderViewModel"

data class ReaderUiState(
    val title: String = "",
    val currentBook: Book? = null,
    val currentChapterFileUrl: String = "",
    val totalChapters: Int = 0,
    val currentChapterIndex: Int = 0,
    val currentReadPosition: Float = 0f,
    val canNavigateNext: Boolean = false,
    val canNavigatePrevious: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ReaderViewModel
    @Inject
    constructor(
        private val repository: BookRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ReaderUiState())
        val uiState = _uiState.asStateFlow()

        private val bookId: String = checkNotNull(savedStateHandle["bookId"])

        init {
            loadBookContent()
        }

        override fun onCleared() {
            saveReadingProgress()
            super.onCleared()
        }

        fun saveReadingProgress() {
        }

        private fun loadBookContent() {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val book: Book? = repository.getBookById(bookId)
                    if (book == null) {
                        Log.e(TAG, "Book with ID $bookId not found.")
                        _uiState.update { it.copy(isLoading = false, error = "Book not found") }
                        return@launch
                    }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            currentBook = book,
                            currentReadPosition = book.lastReadPosition,
                            title = book.title,
                            currentChapterIndex = book.lastReadChapterIndex.coerceAtLeast(0),
                            totalChapters = book.chapters.size,
                            canNavigateNext = book.lastReadChapterIndex < book.chapters.size - 1,
                            canNavigatePrevious = book.lastReadChapterIndex > 0,
                        )
                    }

                    loadChapter(uiState.value.currentChapterIndex)
                } catch (e: Exception) {
                    Log.e(TAG, "Book with ID $bookId not found.")
                    _uiState.update { it.copy(isLoading = false, error = "Book not found") }
                }
            }
        }

        fun loadChapter(chapterIndex: Int) {
            val book = uiState.value.currentBook ?: return

            if (chapterIndex < 0 || chapterIndex >= book.chapters.size) {
                Log.e(
                    TAG,
                    "Invalid chapter index: $chapterIndex. Total chapters: ${book.chapters.size}",
                )
                _uiState.update { it.copy(isLoading = false, error = "Invalid chapter.") }
                return
            }
            _uiState.update { it.copy(isLoading = true, error = null) }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val chapterToLoad: SimpleChapter = book.chapters[chapterIndex]
                    val chapterUrl = repository.getChapterUrl(book.id, chapterToLoad.filePath)

                    _uiState.update {
                        it.copy(
                            currentChapterFileUrl = chapterUrl,
                            isLoading = false,
                            currentChapterIndex = chapterIndex,
                            canNavigateNext = chapterIndex < book.chapters.size - 1,
                            canNavigatePrevious = chapterIndex > 0,
                            currentReadPosition = 0f,
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading chapter $chapterIndex", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load chapter",
                        )
                    }
                }
            }
        }

        fun navigateToNextChapter() {
        }

        fun navigateToPreviousChapter() {
        }
    }
