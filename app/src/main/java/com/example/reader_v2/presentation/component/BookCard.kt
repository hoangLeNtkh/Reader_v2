package com.example.reader_v2.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.reader_v2.domain.model.Book

@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier,
) {
    Column {
        Text(text = book.title)
        book.author?.let {
            Text(text = it)
        }
    }
}
