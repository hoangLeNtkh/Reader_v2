package com.example.reader_v2.presentation.screen.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

private const val  TAG = "LibraryViewModel"

@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {

	fun addBook(uri: Uri) {

	}
}