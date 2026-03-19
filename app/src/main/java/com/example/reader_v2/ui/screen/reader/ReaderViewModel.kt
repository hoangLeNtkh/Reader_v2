package com.example.reader_v2.ui.screen.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.data.repository.ReaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val readerRepository: ReaderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val bookId: String = savedStateHandle.get<String>("bookId")
        ?: throw IllegalStateException("Argument 'bookId' not found in SavedStateHandle. Check your NavGraph route!")

    private val _isBookReady = MutableStateFlow(false)
    val isBookReady = _isBookReady.asStateFlow()

    private val _initialLocator = MutableStateFlow<Locator?>(null)
    val initialLocator = _initialLocator.asStateFlow()

    init {
        Log.d("ViewModelTrace", "ReadViewModel created: ${System.identityHashCode(this)}")
        viewModelScope.launch(Dispatchers.IO) {
            loadBook(bookId)
            _isBookReady.value = true
        }
    }

    suspend fun loadBook(bookId: String) {
        readerRepository.openBook(bookId)
    }
    fun logIdentity(caller: String) {
        Log.d("ViewModelTrace", "$caller is using VM: ${System.identityHashCode(this)}")
    }
}
