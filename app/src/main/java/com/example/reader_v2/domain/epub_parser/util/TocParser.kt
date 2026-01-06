package com.example.reader_v2.domain.epub_parser.util

import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook
import com.example.reader_v2.domain.epub_parser.epub_model.EpubFile
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Singleton
class TocParser
    @Inject
    constructor() {
        suspend fun parse(
            tocFile: EpubFile,
            hrefRootPath: File,
            isNcx: Boolean,
        ): List<EpubBook.TocEntry> =
            withContext(Dispatchers.Default) {
                val rootPath = hrefRootPath.path
                val tocDoc: Document = Ksoup.parseXml(tocFile.data.decodeToString())

                if (isNcx) {
                    parseNcx(tocDoc, rootPath)
                } else {
                    parseNav(tocDoc, rootPath)
                }
            }

        private fun parseNav(
            tocDoc: Document,
            rootPath: String,
        ): List<EpubBook.TocEntry> {
            val tocNav =
                tocDoc.selectFirst("nav")?.firstOrNull {
                    it.attr("epub:type") == "toc" || it.tagName() == "nav" // More flexible
                } ?: throw NoSuchElementException("Nav TOC element not found")

            val ol = tocNav.selectFirst("ol") ?: throw NoSuchElementException("Invalid Nav structure")
            return ol.children().mapNotNull { parseNavRecursively(it, rootPath) }
        }

        private fun parseNcx(
            tocDoc: Document,
            rootPath: String,
        ): List<EpubBook.TocEntry> {
            val navMap =
                tocDoc.selectFirst("navMap")
                    ?: throw NoSuchElementException("ncx navMap not found")

            return navMap.select(" > navPoint").mapNotNull { navPoint ->
                parseNcxRecursively(navPoint, rootPath)
            }
        }
    }

private fun parseNcxRecursively(
    element: Element,
    rootPath: String,
): EpubBook.TocEntry? {
    val title = element.selectFirst("navLabel")?.selectFirst("text")?.ownText() ?: ""
    val href = element.selectFirst("content")?.attr("src")?.decodedUrl ?: ""

    val children =
        element.select(" > navPoint").mapNotNull {
            parseNcxRecursively(it, rootPath)
        }

    val fullHref =
        if (href.isNotEmpty() && !href.startsWith(rootPath)) {
            "$rootPath/$href"
        } else {
            href
        }

    return EpubBook.TocEntry(title = title, link = fullHref, children = children)
}

private fun parseNavRecursively(
    liElement: Element,
    rootPath: String,
): EpubBook.TocEntry? {
    val contentElement: Element? = liElement.selectFirst("a, span")

    val title = contentElement?.ownText()?.trim() ?: ""
    val href =
        if (contentElement?.tagName() == "a") {
            contentElement.attr("href").decodedUrl
        } else {
            ""
        }

    val children =
        liElement.selectFirst("ol")?.let { nestedOrderedList ->
            nestedOrderedList.children().mapNotNull { nestedLiElement ->
                parseNavRecursively(nestedLiElement, rootPath)
            }
        } ?: emptyList()

    if (href.isNotEmpty() || children.isNotEmpty()) {
        val fullHref =
            if (href.isNotEmpty() && !href.startsWith(rootPath)) "$rootPath/$href" else href
        return EpubBook.TocEntry(
            title = title,
            link = fullHref,
            children = children,
        )
    }
    return null
}
