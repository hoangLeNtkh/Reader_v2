package com.example.reader_v2.ui.screen.reader

import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook

@Composable
fun ReaderScreen(readerViewModel: ReaderViewModel = hiltViewModel()) {
    val uiState by readerViewModel.uiState.collectAsState()
    var showBars by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true

                        webViewClient =
                            object : android.webkit.WebViewClient() {
                                override fun onPageFinished(
                                    view: WebView?,
                                    url: String?,
                                ) {
                                    super.onPageFinished(view, url)

                                    view?.applyReaderSettings(readerViewModel.uiState.value.settings)

                                    postDelayed({
                                        uiState.chapterContent?.target?.let { view?.executeScroll(it) }
                                    }, 300)
                                }
                            }

                        setOnScrollChangeListener { v, _, scrollY, _, _ ->
                            val webView = v as WebView
                            val density = webView.resources.displayMetrics.density
                            val totalContentHeight = webView.contentHeight.toFloat() * density
                            val viewportHeight = webView.height.toFloat()
                            val maxScrollY = totalContentHeight - viewportHeight

                            if (maxScrollY > 0) {
                                val progress = (scrollY.toFloat() / maxScrollY).coerceIn(0f, 1f)
                                readerViewModel.updateReadPosition(progress)
                            }
                        }

                        val gestureDetector =
                            GestureDetector(
                                context,
                                object : GestureDetector.SimpleOnGestureListener() {
                                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                                        val webViewWidth = width
                                        val leftEdge = webViewWidth * 0.25f
                                        val rightEdge = webViewWidth * 0.75f

                                        when {
                                            e.x > rightEdge -> readerViewModel.navigateToNextChapter()
                                            e.x < leftEdge -> readerViewModel.navigateToPreviousChapter()
                                            else -> showBars = !showBars
                                        }
                                        return true
                                    }
                                },
                            )
                        setOnTouchListener { _, event ->
                            gestureDetector.onTouchEvent(event)
                            false
                        }
                    }
                },
                update = { webView ->
                    webView.applyReaderSettings(uiState.settings)

                    uiState.chapterContent?.let { content ->
                        if (webView.url != content.url) {
                            webView.loadUrl(content.url)
                        } else {
                            webView.executeScroll(content.target)
                        }
                    }
                },
            )

            AnimatedVisibility(
                visible = showBars,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
            ) {
                ReaderTopBar(
                    title = uiState.title,
                    onTocClick = { readerViewModel.toggleTocVisibility() },
                    onSettingsClick = { readerViewModel.toggleSettingsVisibility() },
                    onBackClick = { /* TODO: Navigate back */ },
                )
            }

            AnimatedVisibility(
                visible = showBars,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                ReaderBottomBar(
                    uiState = uiState,
                    onPreviousClick = { readerViewModel.navigateToPreviousChapter() },
                    onNextClick = { readerViewModel.navigateToNextChapter() },
                )
            }

            // Table of Contents Dropdown
            if (uiState.isTocVisible) {
                uiState.currentBook?.let { book ->
                    TableOfContentsDropdown(
                        toc = book.toc,
                        onItemSelected = { tocEntry ->
                            readerViewModel.navigateToChapterByToc(tocEntry)
                        },
                        onDismiss = { readerViewModel.toggleTocVisibility() },
                    )
                }
            }

            if (uiState.isSettingsVisible) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { readerViewModel.toggleSettingsVisibility() },
                ) {
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .clickable(enabled = false) {},
                    ) {
                        SettingsOverlay(
                            settings = uiState.settings,
                            onSettingsChange = { readerViewModel.updateReaderSettings(it) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderTopBar(
    title: String,
    onTocClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onTocClick) {
                Icon(Icons.Default.Menu, contentDescription = "Table of Contents")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            ),
    )
}

@Composable
fun ReaderBottomBar(
    uiState: ReaderUiState,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onPreviousClick,
                enabled = uiState.canNavigatePrevious,
            ) {
                Text("Previous")
            }
            Text("Chapter ${uiState.currentChapterIndex + 1} / ${uiState.totalChapters}")
            Button(
                onClick = onNextClick,
                enabled = uiState.canNavigateNext,
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun TableOfContentsDropdown(
    toc: List<EpubBook.TocEntry>,
    onItemSelected: (EpubBook.TocEntry) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .clickable(enabled = false) {},
            // Prevent background click through
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            LazyColumn {
                item {
                    Text(
                        text = "Table of Contents",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                items(toc) { tocEntry ->
                    TocItem(entry = tocEntry, onItemSelected = onItemSelected)
                }
            }
        }
    }
}

@Composable
fun TocItem(
    entry: EpubBook.TocEntry,
    onItemSelected: (EpubBook.TocEntry) -> Unit,
    level: Int = 0,
) {
    val isJumpable = entry.link.isNotBlank()

    Column {
        Text(
            text = entry.title,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isJumpable) { onItemSelected(entry) }
                    .padding(
                        start = (16 * (level + 1)).dp,
                        top = 12.dp,
                        bottom = 12.dp,
                        end = 16.dp,
                    ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color =
                if (isJumpable) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    textDecoration =
                        if (isJumpable) {
                            TextDecoration.Underline
                        } else {
                            TextDecoration.None
                        },
                ),
        )
        if (entry.children.isNotEmpty()) {
            entry.children.forEach { child ->
                TocItem(entry = child, onItemSelected = onItemSelected, level = level + 1)
            }
        }
    }
}

@Composable
fun SettingsOverlay(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("A", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = settings.fontSize.toFloat(),
                    onValueChange = { onSettingsChange(settings.copy(fontSize = it.toInt())) },
                    valueRange = 12f..36f,
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                )
                Text("A", style = MaterialTheme.typography.titleLarge)
            }

            Row {
                ReaderTheme.entries.forEach { theme ->
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .background(Color(theme.backgroundColor.toColorInt()))
                                .clickable { onSettingsChange(settings.copy(theme = theme)) },
                    )
                }
            }
        }
    }
}
