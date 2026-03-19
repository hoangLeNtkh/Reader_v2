package com.example.reader_v2.data.repository

import android.content.Context
import android.net.Uri
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.util.BookFileDataSource
import com.example.reader_v2.data.entity.BookEntity
import com.example.reader_v2.data.model.Book
import com.example.reader_v2.data.util.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser

@Singleton
class BookRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val fileDataSource: BookFileDataSource,
    private val bookDao: BookDao,
    private val utility: Utility
) : BookRepository {
    override fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks().map { bookList ->
		bookList.map {
			it.toModel()
		}
	}

    override suspend fun addBook(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
		return@withContext try {
			val bookId = utility.calculateChecksum(uri)

			val existingBook = bookDao.getBook(bookId)
			if (existingBook?.id == bookId) {
				return@withContext Result.success(bookId)
			}

			val bookFile = fileDataSource.saveBookToAppStorage(uri, bookId)

			val httpClient = DefaultHttpClient()
			val assetRetriever = AssetRetriever(
				contentResolver = context.contentResolver,
				httpClient = httpClient
			)
			val asset = assetRetriever.retrieve(bookFile).getOrElse {
				throw Exception("Failed to retrieve asset: $it")
			}
			val pdfFactory: PdfiumDocumentFactory? = try {
				PdfiumDocumentFactory(context)
			} catch (e: Exception) {
				null
			}
			val publicationOpener = PublicationOpener(
				publicationParser = DefaultPublicationParser(
					context,
					httpClient = httpClient,
					assetRetriever = assetRetriever,
					pdfFactory = pdfFactory
				)
			)

			val publication = publicationOpener.open(asset, allowUserInteraction = false).getOrElse {
				throw Exception("Failed to open publication: $it")
			}
			val coverImagePath = publication.cover().let { bitmap ->
				fileDataSource.saveCoverImage(bookId, bitmap)
			}

		    val bookEntity = BookEntity(
			    id = bookId,
			    filePath = bookFile.absolutePath,
			    title = publication.metadata.title,
			    contributors = publication.metadata.authors,
			    description = publication.metadata.description,
			    coverImagePath = coverImagePath,
			    progression = 0f,
			    lastReadLocation = null,
			    addedDate = System.currentTimeMillis(),
			    lastReadDate = System.currentTimeMillis(),
		    )
		    bookDao.addBook(bookEntity)
		    publication.close()
		    asset.close()

		    Result.success(bookEntity.id)
	    } catch (e: Exception) {
		    Result.failure(e)
	    }
	}

    override suspend fun deleteBook(bookId: String) {
        withContext(Dispatchers.IO) {
            bookDao.deleteBook(bookId)
            fileDataSource.deleteBook(bookId)
        }
    }

	override suspend fun updateBook(
		bookId: String,
		lastReadLocation: Locator,
		lastReadDate: Long
	) {
		TODO("Not yet implemented")
	}

	private fun BookEntity.toModel(): Book = Book(
		id = id,
		filePath = filePath,
		title = title,
		contributors = contributors,
		description = description,
		coverImagePath = coverImagePath,
		progression = progression,
		lastReadLocation = lastReadLocation,
		addedDate = addedDate,
		lastReadDate = lastReadDate,
	)
}
