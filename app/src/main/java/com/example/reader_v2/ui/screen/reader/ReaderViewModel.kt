package com.example.reader_v2.ui.screen.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.repository.ReaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator

data class ReaderUiState(
    val bookId: String = ""
)

@HiltViewModel
class ReaderViewModel
@Inject
constructor(
    private val readerRepository: ReaderRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState = _uiState.asStateFlow()
    private val _isBookLoaded = MutableStateFlow(false)
    val isBookLoaded = _isBookLoaded.asStateFlow()

    var temporaryLocator: Locator? = savedStateHandle["last_loc"]

    suspend fun initialize(bookId: String) {
        readerRepository.openBook(bookId)
        _uiState.value = _uiState.value.copy(bookId)
        if (temporaryLocator == null) {
            temporaryLocator = readerRepository.getSavedLocation(_uiState.value.bookId)
        }
        _isBookLoaded.value = true
    }

    fun updateTemporaryLocation(locator: Locator) {
        temporaryLocator = locator
        savedStateHandle["last_loc"] = locator
    }

    fun saveReadingProgression() {
        temporaryLocator?.let { lastLoc ->
            viewModelScope.launch {
                readerRepository.saveReadingProgression(
                    bookId = _uiState.value.bookId,
                    locator = lastLoc,
                    progression = lastLoc.locations.totalProgression
                )
            }
        }
    }
}
