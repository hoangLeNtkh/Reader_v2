package com.example.reader_v2.data.data_source

import android.content.Context
import android.net.Uri
import com.example.reader_v2.domain.epub_parser.util.getEntries
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipInputStream

@Singleton
class BookFileDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val booksDir =
            File(context.filesDir, "books").apply {
                if (!exists()) mkdirs()
            }
        private val extractedBooksDir =
            File(context.filesDir, "extracted_books").apply {
                if (!exists()) mkdirs()
            }

        fun saveBookToAppStorage(
            uri: Uri,
            bookId: String,
        ): File {
            val bookFile = File(booksDir, "$bookId.epub")

            context.contentResolver.openInputStream(uri)!!.buffered().let { inputStream ->
                bookFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return bookFile
        }

        suspend fun extractEpub(
            epubFile: File,
            bookId: String,
        ) = withContext(Dispatchers.IO) {
            val destinationDir = File(extractedBooksDir, bookId)
            if (destinationDir.exists()) destinationDir.deleteRecursively()
            destinationDir.mkdirs()

            ZipInputStream(epubFile.inputStream()).use { zipInput ->
                zipInput
                    .getEntries()
                    .filterNot { it.isDirectory }
                    .forEach { zipEntry ->
                        val file = File(destinationDir, zipEntry.name)
                        file.parentFile?.mkdirs()
                        file.outputStream().use { fileOutput ->
                            zipInput.copyTo(fileOutput)
                        }
                    }
            }
        }

        fun getChapterFileUrl(
            bookId: String,
            chapterFilePath: String,
        ): String {
            val chapterFile = File(getExtractedBookPath(bookId), chapterFilePath)
            if (!chapterFile.exists()) {
                throw FileNotFoundException("Chapter file not found: ${chapterFile.absolutePath}")
            }
            return "file://${chapterFile.absolutePath}"
        }

        fun getExtractedBookPath(bookId: String): File = File(extractedBooksDir, bookId)

        fun deleteBook(bookId: String) {
            val extractedDir = File(context.filesDir, "extracted/$bookId")
            if (extractedDir.exists()) {
                extractedDir.deleteRecursively()
            }

            // 2. Delete the original saved .epub file
            val epubFile = File(context.filesDir, "books/$bookId.epub")
            if (epubFile.exists()) {
                epubFile.delete()
            }
        }
    }
