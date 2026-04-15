package com.example.reader_v2.data.repository

import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

interface ReaderRepository {
	val publication: Publication?
	val navigatorFactory: EpubNavigatorFactory?
	@OptIn(ExperimentalReadiumApi::class)
	var navigator: VisualNavigator?

	suspend fun openBook(bookId: String): Result<String>
	fun closeBook()
	suspend fun getSavedLocation(bookId: String): Locator?
	suspend fun saveReadingProgression(bookId: String, locator: Locator?, progression: Double?)
}