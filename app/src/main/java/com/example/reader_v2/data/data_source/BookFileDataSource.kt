package com.example.reader_v2.data.data_source

import android.content.Context
import android.net.Uri
import com.example.reader_v2.epub_parser.utils.getEntries
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
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

        suspend fun saveBookToAppStorage(
	        uri: Uri,
	        bookId: String,
        ): File =
	        withContext(Dispatchers.IO) {
		        val bookFile = File(booksDir, "$bookId.epub")
		        context.contentResolver.openInputStream(uri)?.use { inputStream ->
			        bookFile.outputStream().use { outputStream ->
				        inputStream.copyTo(outputStream)
			        }
		        } ?: throw IOException("Failed to save book with URI: $uri")
		        bookFile
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
    }