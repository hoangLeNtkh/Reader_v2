package com.example.reader_v2.data.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

@Singleton
class BookFileDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val booksDir = File(context.filesDir, "books").apply {
        if (!exists()) mkdirs()
    }
    private val coversDir = File(context.filesDir, "covers").apply {
        if (!exists()) mkdirs()
    }

    fun saveBookToAppStorage(uri: Uri, bookId: String): File {
        val bookFile = File(booksDir, "$bookId.epub")
        context.contentResolver.openInputStream(uri)!!.buffered().let { inputStream ->
            bookFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return bookFile
    }

    fun saveCoverImage(bookId: String, bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val coverFile = File(coversDir, "$bookId.webp")
        coverFile.outputStream().use { outStream ->
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outStream)
        }

	    return coverFile.absolutePath
    }

    fun getBookFile(bookId: String): File {
        return File(booksDir, "$bookId.epub")
    }
    fun deleteBook(bookId: String) {
        val epubFile = File(context.filesDir, "books/$bookId.epub")
        if (epubFile.exists()) {
            epubFile.delete()
        }
    }
}