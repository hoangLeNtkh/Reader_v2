package com.example.reader_v2.ui.screen.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook
import com.example.reader_v2.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ReaderViewModel"

data class ReaderUiState(
    val title: String = "",
    val currentBook: Book? = null,
    val totalChapters: Int = 0,
    val chapterContent: ChapterContent? = null,
    val currentChapterIndex: Int = 0,
    val currentReadPosition: Float = 0f,
    val canNavigateNext: Boolean = false,
    val canNavigatePrevious: Boolean = false,
    val isTocVisible: Boolean = false,
    val settings: ReaderSettings = ReaderSettings(),
    val isSettingsVisible: Boolean = false,
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
        private var scrollJob: kotlinx.coroutines.Job? = null
        private var lastScrollPosition: Float = 0f

        init {
            loadBookContent()
        }

        override fun onCleared() {
            val state = uiState.value
            val book = state.currentBook ?: return

            viewModelScope.launch(Dispatchers.IO + NonCancellable) {
                repository.updateReadingProgress(
                    bookId = book.id,
                    chapterIndex = state.currentChapterIndex,
                    position = lastScrollPosition,
                )
            }
            super.onCleared()
        }

        fun saveReadingProgress() {
            val state = uiState.value
            val book = state.currentBook ?: return
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateReadingProgress(
                    bookId = book.id,
                    chapterIndex = state.currentChapterIndex,
                    position = state.currentReadPosition,
                )
            }
        }

        private fun loadBookContent() {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                val book: Book = repository.getBook(bookId)

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        currentBook = book,
                        title = book.title,
                        totalChapters = book.chapters.size,
                        canNavigateNext = book.lastReadChapterIndex < book.chapters.size - 1,
                        canNavigatePrevious = book.lastReadChapterIndex > 0,
                        currentReadPosition = book.lastReadPosition,
                        currentChapterIndex = book.lastReadChapterIndex.coerceAtLeast(0),
                    )
                }

                loadChapter(
                    chapterIndex = uiState.value.currentChapterIndex,
                    initialPosition = book.lastReadPosition,
                )
            }
        }

        fun loadChapter(
            chapterIndex: Int,
            initialPosition: Float = 0f,
        ) {
            val book = uiState.value.currentBook ?: return

            _uiState.update { it.copy(isLoading = true, error = null) }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val chapterToLoad: EpubBook.Chapter = book.chapters[chapterIndex]
                    val chapterUrl = repository.getFileUrl(book.id, chapterToLoad.filePath)

                    _uiState.update {
                        it.copy(
                            chapterContent =
                                ChapterContent(
                                    url = chapterUrl,
                                    target = if (initialPosition > 0f) ScrollTarget.Position(initialPosition) else ScrollTarget.None,
                                ),
                            currentChapterIndex = chapterIndex,
                            currentReadPosition = initialPosition,
                            canNavigateNext = chapterIndex < book.chapters.size - 1,
                            canNavigatePrevious = chapterIndex > 0,
                            isLoading = false,
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
            val currentIndex = _uiState.value.currentChapterIndex
            val chapterSize = _uiState.value.totalChapters

            if (currentIndex < chapterSize - 1) {
                val nextIndex = currentIndex + 1
                loadChapter(nextIndex)
            } else {
                Log.d(TAG, "Already at the last chapter.")
            }
        }

        fun navigateToPreviousChapter() {
            val currentIndex = _uiState.value.currentChapterIndex

            if (currentIndex > 0) {
                val previousIndex = currentIndex - 1
                loadChapter(previousIndex)
            } else {
                Log.d(TAG, "Already at the first chapter.")
            }
        }

        fun toggleTocVisibility() {
            _uiState.update { it.copy(isTocVisible = !it.isTocVisible) }
        }

        fun navigateToChapterByToc(tocEntry: EpubBook.TocEntry) {
            val book = uiState.value.currentBook ?: return

            val parts = tocEntry.link.split("#")
            val linkPath = parts[0].removePrefix("./")
            val anchor = if (parts.size > 1) parts[1] else null

            val chapterIndex =
                book.chapters.indexOfFirst {
                    it.filePath.removePrefix("./") == linkPath
                }

            if (chapterIndex != -1) {
                val chapterUrl = repository.getFileUrl(book.id, book.chapters[chapterIndex].filePath)
                val newTarget = if (anchor != null) ScrollTarget.Anchor(anchor) else ScrollTarget.None

                _uiState.update {
                    it.copy(
                        chapterContent =
                            ChapterContent(
                                url = chapterUrl,
                                target = newTarget,
                            ),
                        currentChapterIndex = chapterIndex,
                        isTocVisible = false,
                    )
                }
            }
        }

        fun updateReadPosition(position: Float) {
            lastScrollPosition = position

            scrollJob?.cancel()
            scrollJob =
                viewModelScope.launch {
                    kotlinx.coroutines.delay(200)
                    _uiState.update { it.copy(currentReadPosition = position) }
                    saveReadingProgress()
                }
        }

        fun toggleSettingsVisibility() {
            _uiState.update { it.copy(isSettingsVisible = !it.isSettingsVisible) }
        }

        fun updateReaderSettings(newSettings: ReaderSettings) {
            _uiState.update { it.copy(settings = newSettings) }
        }
    }
