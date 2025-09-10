package com.example.reader_v2.epub_parser.helpers

import com.example.reader_v2.epub_parser.model.Chapter
import com.example.reader_v2.epub_parser.model.ManifestItem
import com.example.reader_v2.epub_parser.model.TocEntry
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
		manifestItems: Map<String, ManifestItem>,
		tocEntries: List<TocEntry>,
		spine: Element
	) {
		return withContext(Dispatchers.Default) {
			val chapters = mutableListOf<Chapter>()

			spine.children().forEach { itemRefElement ->
				val itemIdRef: String = itemRefElement.attr("idref")
				val manifestItem: ManifestItem? = manifestItems[itemIdRef]
				val rootPath: String = hrefRootPath.path

				if (manifestItem != null && isChapter(manifestItem)) {
					var manifestItemFullHref: String = manifestItem.hrefFullPath

					if (!manifestItemFullHref.startsWith(rootPath)) {
						manifestItemFullHref = "$rootPath/$manifestItemFullHref"
					}

					val tocEntry: TocEntry? =
						findTocEntryForChapter(tocEntries, manifestItemFullHref)
					chapters.add(
						Chapter(
							title = tocEntry?.chapterTitle ?: "Chapter ${chapters.size + 1}",
							fragmentLink = tocEntry?.chapterLink ?: manifestItemFullHref,
							filePath = manifestItemFullHref
						)
					)
				}
			}
		}
	}

	private fun isChapter(item: ManifestItem): Boolean {
		val extension = item.hrefFullPath.substringAfterLast('.').lowercase()
		return extension in listOf("xhtml", "xml", "html", "htm")
	}

	private fun findTocEntryForChapter(
		tocEntries: List<TocEntry>,
		chapterHref: String
	): TocEntry? {
		val chapterHrefWithoutFragment = chapterHref.substringBefore('#')
		return searchRecursively(tocEntries, chapterHrefWithoutFragment)
	}

	private fun searchRecursively(
		entries: List<TocEntry>,
		chapterHrefWithoutFragment: String
	): TocEntry? {
		for (entry in entries) {
			if (entry.chapterLink
				.substringBefore('#')
				.equals(chapterHrefWithoutFragment, ignoreCase = true)
			) {
				return entry
			}
			val foundInChildren: TocEntry? =
				searchRecursively(entry.children, chapterHrefWithoutFragment)
			if (foundInChildren != null) {
				return foundInChildren
			}
		}
		return null
	}
}