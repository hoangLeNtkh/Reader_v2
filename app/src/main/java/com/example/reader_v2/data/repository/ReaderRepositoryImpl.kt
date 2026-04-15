package com.example.reader_v2.data.repository

import android.content.Context
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.util.BookFileDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser

@Singleton
class ReaderRepositoryImpl
@Inject
constructor (
	@ApplicationContext
	private val context: Context,
	private val fileDataSource: BookFileDataSource,
	private val bookDao: BookDao,
) : ReaderRepository {
	override var publication: Publication? = null
	override var navigatorFactory: EpubNavigatorFactory? = null
	@OptIn(ExperimentalReadiumApi::class)
	override var navigator: VisualNavigator? = null

	@OptIn(
		ExperimentalReadiumApi::class)
	override suspend fun openBook(bookId: String): Result<String> = withContext(Dispatchers.IO) {
		return@withContext try {
			val bookFile = fileDataSource.getBookFile(bookId)

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
					context = context,
					httpClient = httpClient,
					assetRetriever = assetRetriever,
					pdfFactory = pdfFactory
				)
			)
			val publication = publicationOpener
				.open(asset, allowUserInteraction = false)
				.getOrElse { throw Exception("Failed to open publication: $it") }

			this@ReaderRepositoryImpl.publication = publication
			this@ReaderRepositoryImpl.navigatorFactory =
				EpubNavigatorFactory(
					publication = publication,
					configuration = EpubNavigatorFactory
						.Configuration(defaults = EpubDefaults(pageMargins = 1.4))
			)

			Result.success(bookId)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override fun closeBook() {
		publication?.close()
		navigatorFactory = null
		navigator = null
	}

	override suspend fun saveReadingProgression(
		bookId: String,
		locator: Locator?,
		progression: Double?
	) {
		val jsonString = locator?.toJSON().toString()
		bookDao.updateBook(
			bookId = bookId,
			lastReadPositionLocator = jsonString,
			progression = progression,
			lastReadDate = System.currentTimeMillis()
		)
	}

	override suspend fun getSavedLocation(bookId: String): Locator? = withContext(Dispatchers.IO) {
		val jsonString = bookDao.getLocatorJson(bookId) ?: return@withContext null
		return@withContext try {
			Locator.fromJSON(JSONObject(jsonString))
		} catch (e: Exception) {
			null
		}
	}
}
