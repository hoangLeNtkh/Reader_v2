package com.example.reader_v2.presentation.screen.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
	libViewModel: LibraryViewModel = hiltViewModel()
) {
	val scope = rememberCoroutineScope()
	val filePickerScreenLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocument(),
		onResult = { uri ->
			uri?.let {
				scope.launch {
					libViewModel.addBook(it)
				}
			}
		}
	)


	Scaffold(
		modifier = Modifier.fillMaxSize(),
	) {
		innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)) {
			Button(
				onClick = {
					filePickerScreenLauncher.launch(arrayOf("application/epub+zip"))
				}
			) {
				Text(text = "Select Epub File")
			}
		}
	}
}