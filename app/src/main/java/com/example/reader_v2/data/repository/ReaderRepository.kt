package com.example.reader_v2.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

interface ReaderRepository {
	val publication: Publication?
	val navigatorFactory: EpubNavigatorFactory?
	var navigator: VisualNavigator?
	val currentLocator: StateFlow<Locator?>

	suspend fun openBook(bookId: String): Result<String>
	fun closeBook()

	suspend fun getSavedLocation(bookId: String): Locator?

	fun emitLocation(bookId: String, locator: Locator)
}