package com.example.reader_v2.data.repository

import android.content.Context
import android.net.Uri
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.data_source.BookFileDataSource
import com.example.reader_v2.data.entity.BookEntity
import com.example.reader_v2.data.entity.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.security.MessageDigest

@Singleton
class BookRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val fileDataSource: BookFileDataSource,
    private val bookDao: BookDao,
) : BookRepository {
    override fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks().map {
        bookList -> bookList.map { it.toModel() }
    }

    override suspend fun getBook(bookId: String): Book = bookDao.getBookById(bookId)!!.toModel()

    override suspend fun addBook(uri: Uri): String? = withContext(Dispatchers.IO) {
        val bookId = calculateChecksum(uri)

        val existingBook = bookDao.getBookById(bookId)
        if (existingBook != null) {
            return@withContext existingBook.title
        }

        val bookFile = fileDataSource.saveBookToAppStorage(uri, bookId)

        val httpClient = DefaultHttpClient()
        val assetRetriever = AssetRetriever(
            contentResolver = context.contentResolver,
            httpClient = httpClient
        )
        val asset = assetRetriever.retrieve(bookFile)
            .getOrElse { throw Exception("Failed to retrieve asset: $it") }

        val publicationOpener = PublicationOpener(
            publicationParser = DefaultPublicationParser(
                context,
                httpClient = httpClient,
                assetRetriever = assetRetriever,
                pdfFactory = PdfiumDocumentFactory(context)
            )
        )
        val publication = publicationOpener.open(asset, allowUserInteraction = false)
            .getOrElse { throw Exception("Failed to open publication: $it") }

        val coverImagePath = publication.cover().let { bitmap ->
            fileDataSource.saveCoverImage(bookId, bitmap)
        }

        val bookEntity = BookEntity(
            id = bookId,
            title = publication.metadata.title,
            author = publication.metadata.authors,
            description = publication.metadata.description,
            coverPath = coverImagePath,
            filePath = bookFile.absolutePath,
            dateAdded = System.currentTimeMillis(),
            lastReadDate = System.currentTimeMillis(),
        )
        bookDao.insertBook(bookEntity)

        return@withContext bookEntity.title ?: ""
    }

    override fun getFileUrl(
        bookId: String,
        chapterFilePath: String,
    ): String = fileDataSource.getChapterFileUrl(bookId, chapterFilePath)

    override suspend fun updateReadingProgress(
        bookId: String,
        chapterIndex: Int,
        position: Float,
    ) {
        withContext(Dispatchers.IO) {
            bookDao.updateReadingProgress(
                bookId = bookId,
                chapterIndex = chapterIndex,
                position = position,
            )
        }
    }

    private fun calculateChecksum(uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.contentResolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(8192)
            var bytesRead = input.read(buffer)
            while (bytesRead != -1) {
                digest.update(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    override suspend fun deleteBook(bookId: String) {
        withContext(Dispatchers.IO) {
            bookDao.deleteBookById(bookId)
            fileDataSource.deleteBook(bookId)
        }
    }

    private fun BookEntity.toModel(): Book = Book(
	    id = id,
	    filePath = filePath,
	    title = title,
	    author = author,
	    description = description,
	    coverPath = coverPath,
	    dateAdded = dateAdded,
	    lastReadDate = lastReadDate,
    )
}
