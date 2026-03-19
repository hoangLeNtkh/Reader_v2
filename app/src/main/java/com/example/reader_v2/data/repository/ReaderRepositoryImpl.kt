package com.example.reader_v2.data.repository

import android.content.Context
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.util.BookFileDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
class ReaderRepositoryImpl @Inject constructor (
	@ApplicationContext
	private val context: Context,
	private val fileDataSource: BookFileDataSource,
	private val bookDao: BookDao,
) : ReaderRepository {
	override var publication: Publication? = null
	override var navigatorFactory: EpubNavigatorFactory? = null
	override var navigator: VisualNavigator? = null

	private val _currentLocator = MutableStateFlow<Locator?>(null)
	override val currentLocator = _currentLocator.asStateFlow()

	private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
	private val saveRequests = MutableSharedFlow<Pair<String, Locator>>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	init {
		repositoryScope.launch {
			saveRequests.collectLatest { (bookId, locator) ->
				bookDao.updateBook(bookId, locator.toJSON().toString(), System.currentTimeMillis())
			}
		}
	}

	@OptIn(ExperimentalReadiumApi::class)
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
					context,
					httpClient = httpClient,
					assetRetriever = assetRetriever,
					pdfFactory = pdfFactory
				)
			)
			val publication =
				publicationOpener.open(asset, allowUserInteraction = false).getOrElse {
					throw Exception("Failed to open publication: $it")
				}

			this@ReaderRepositoryImpl.publication = publication

			this@ReaderRepositoryImpl.navigatorFactory = EpubNavigatorFactory(
				publication = publication,
				configuration = EpubNavigatorFactory.Configuration(
					defaults = EpubDefaults(
						pageMargins = 1.4
					)
				)
			)

			Result.success(bookId)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override fun closeBook() {
		publication?.close()
		publication = null
		navigatorFactory = null
	}

	override suspend fun getSavedLocation(bookId: String): Locator? = withContext(Dispatchers.IO) {
		val jsonString = bookDao.getLocatorJson(bookId) ?: return@withContext null
		return@withContext try {
			Locator.fromJSON(JSONObject(jsonString))
		} catch (e: Exception) {
			null
		}
	}

	override fun emitLocation(bookId: String, locator: Locator) {
		_currentLocator.value = locator
		saveRequests.tryEmit(bookId to locator)
	}
}
