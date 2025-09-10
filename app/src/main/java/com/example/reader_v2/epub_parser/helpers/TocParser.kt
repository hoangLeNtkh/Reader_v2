package com.example.reader_v2.epub_parser.helpers

import com.example.reader_v2.epub_parser.model.EpubFile
import com.example.reader_v2.epub_parser.model.TocEntry
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Singleton
class TocParser @Inject constructor() {
	suspend fun parse(tocFile: EpubFile, hrefRootPath: File): List<TocEntry> {
		return withContext(Dispatchers.Default) {
			val rootPath = hrefRootPath.path

			val tocDoc: Document = Ksoup.parseXml(tocFile.data.decodeToString())
			val tocNavMap: Element = tocDoc.selectFirst("nav")?.firstOrNull() {
				it.attr("epub:type") == "toc"
			} ?: throw NoSuchElementException("Toc element not found")

			val topLevelOrderedList: Element = tocNavMap.selectFirst("ol")
				?: throw NoSuchElementException("Invalid toc structure")

			topLevelOrderedList.children().mapNotNull { liElement ->
				parseRecursively(liElement, rootPath)
			}
		}
	}

	private fun parseRecursively(liElement: Element, rootPath: String): TocEntry? {
		val contentElement: Element? = liElement.selectFirst("a, span")

		val title = contentElement?.ownText()?.trim() ?: ""
		val href = if (contentElement?.tagName() == "a") {
			contentElement.attr("href").decodedUrl
		} else {
			""
		}

		val children = liElement.selectFirst("ol")?.let { nestedOrderedList ->
			nestedOrderedList.children().mapNotNull { nestedLiElement ->
				parseRecursively(nestedLiElement, rootPath)
			}
		} ?: emptyList()

		if (href.isNotEmpty() || children.isNotEmpty()) {
			val fullHref = if (href.isNotEmpty() && !href.startsWith(rootPath)) "$rootPath/$href" else href
			return TocEntry(chapterTitle = title, chapterLink = fullHref, children = children)
		}
		return null
	}
}