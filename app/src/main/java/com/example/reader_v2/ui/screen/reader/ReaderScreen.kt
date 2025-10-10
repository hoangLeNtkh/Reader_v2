package com.example.reader_v2.ui.screen.reader

import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReaderScreen(readerViewModel: ReaderViewModel = hiltViewModel()) {
    val uiState by readerViewModel.uiState.collectAsState()
    val currentChapterUrl = uiState.currentChapterFileUrl

    Scaffold { innerPadding ->
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                }
            },
            update = { webView ->
                currentChapterUrl.let {
                    webView.loadUrl(it)
                }
            },
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                onClick = { readerViewModel.navigateToPreviousChapter() },
                enabled = uiState.canNavigatePrevious,
            ) {
                Text("Previous")
            }
            Text("Chapter ${uiState.currentChapterIndex + 1} / ${uiState.totalChapters}")
            Button(
                onClick = { readerViewModel.navigateToNextChapter() },
                enabled = uiState.canNavigateNext,
            ) {
                Text("Next")
            }
        }
    }
}
