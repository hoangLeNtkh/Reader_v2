package com.example.reader_v2.data.repository

import android.net.Uri
import com.example.reader_v2.data.model.Book
import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Locator

interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>

    suspend fun addBook(uri: Uri): Result<String>

    suspend fun deleteBook(bookId: String)

    suspend fun updateBook(
        bookId: String,
        lastReadLocation: Locator,
        lastReadDate: Long
    )
}
