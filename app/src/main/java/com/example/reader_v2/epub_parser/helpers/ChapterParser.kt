package com.example.reader_v2.epub_parser.helpers

import com.example.reader_v2.epub_parser.model.EpubBook
import com.fleeksoft.ksoup.nodes.Element
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Singleton
class ChapterParser @Inject constructor() {
	suspend fun parse(
		hrefRootPath: File,
		manifestItems: Map<String, EpubBook.ManifestItem>,
		tocEntries: List<EpubBook.TocEntry>,
		spine: Element
	): List<EpubBook.Chapter> = withContext(Dispatchers.Default) {
		val chapters = mutableListOf<EpubBook.Chapter>()

		spine.children().forEach { itemRefElement ->
			val itemIdRef: String = itemRefElement.attr("idref")
			val manifestItem: EpubBook.ManifestItem? = manifestItems[itemIdRef]
			val rootPath: String = hrefRootPath.path

			if (manifestItem != null && isChapter(manifestItem)) {
				var manifestItemFullHref: String = manifestItem.hrefFullPath

				if (!manifestItemFullHref.startsWith(rootPath)) {
					manifestItemFullHref = "$rootPath/$manifestItemFullHref"
				}

				val tocEntry: EpubBook.TocEntry? = findTocEntryForChapter(tocEntries, manifestItemFullHref)
				chapters.add(
					EpubBook.Chapter(
						title = tocEntry?.chapterTitle ?: "Chapter ${chapters.size + 1}",
						fragmentLink = tocEntry?.chapterLink ?: manifestItemFullHref,
						filePath = manifestItemFullHref
					)
				)
			}
		}
		chapters
	}

	private fun isChapter(item: EpubBook.ManifestItem): Boolean {
		val extension = item.hrefFullPath.substringAfterLast('.').lowercase()
		return extension in listOf("xhtml", "xml", "html", "htm")
	}

	private fun findTocEntryForChapter(
		tocEntries: List<EpubBook.TocEntry>,
		chapterHref: String
	): EpubBook.TocEntry? {
		val chapterHrefWithoutFragment = chapterHref.substringBefore('#')
		return searchRecursively(tocEntries, chapterHrefWithoutFragment)
	}

	private fun searchRecursively(
		entries: List<EpubBook.TocEntry>,
		chapterHrefWithoutFragment: String
	): EpubBook.TocEntry? {
		for (entry in entries) {
			if (entry.chapterLink.substringBefore('#')
					.equals(chapterHrefWithoutFragment, ignoreCase = true)
			) {
				return entry
			}
			val foundInChildren: EpubBook.TocEntry? =
				searchRecursively(entry.children, chapterHrefWithoutFragment)
			if (foundInChildren != null) {
				return foundInChildren
			}
		}
		return null
	}
}