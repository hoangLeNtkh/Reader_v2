package com.example.reader_v2.data.repository

import android.net.Uri
import com.example.reader_v2.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>

    suspend fun getBook(bookId: String): Book

    suspend fun addAndExtractBook(uri: Uri): String

    fun getFileUrl(
        bookId: String,
        chapterFilePath: String,
    ): String

    suspend fun updateReadingProgress(
        bookId: String,
        chapterIndex: Int,
        position: Float,
    )

    suspend fun deleteBook(bookId: String)
}
