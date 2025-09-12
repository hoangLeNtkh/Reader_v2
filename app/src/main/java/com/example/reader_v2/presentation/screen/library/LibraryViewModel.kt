package com.example.reader_v2.presentation.screen.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reader_v2.domain.AddAndExtractBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel @Inject constructor(
	private val addAndExtractBookUseCase: AddAndExtractBookUseCase
) : ViewModel() {
	fun addAndExtractBook(uri: Uri) {
		viewModelScope.launch {
			addAndExtractBookUseCase(uri)
		}
	}
}