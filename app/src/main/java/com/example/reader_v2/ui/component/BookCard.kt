package com.example.reader_v2.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.reader_v2.domain.model.Book
import java.io.File

@Composable
fun BookCard(
    modifier: Modifier = Modifier,
    book: Book,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(book.coverPath)
                        .crossfade(true)
                        .build(),
                contentDescription = "Cover of ${book.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

//    Text(
//        text = book.title,
//        style = MaterialTheme.typography.bodyMedium,
//        textAlign = TextAlign.Center,
//        maxLines = 2,
//        overflow = TextOverflow.Ellipsis,
//        modifier = Modifier.fillMaxWidth(),
//    )
}
