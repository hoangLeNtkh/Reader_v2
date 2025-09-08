package com.example.reader_v2.presentation.screen.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LibraryScreen(

) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
	) {
		innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)) {
			Button() {
				Text(text = "Select Epub File")
			}
		}
	}
}