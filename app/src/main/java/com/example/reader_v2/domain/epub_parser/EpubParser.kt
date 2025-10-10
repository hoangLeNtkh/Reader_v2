package com.example.reader_v2.domain.epub_parser

import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook
import com.example.reader_v2.domain.epub_parser.epub_model.EpubFile
import com.example.reader_v2.domain.epub_parser.util.ChapterParser
import com.example.reader_v2.domain.epub_parser.util.TocParser
import com.example.reader_v2.domain.epub_parser.util.ZippedFilesReader
import com.example.reader_v2.domain.epub_parser.util.decodedUrl
import com.example.reader_v2.domain.epub_parser.util.findHrefFullPath
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import kotlin.collections.get

@Singleton
class EpubParser
    @Inject
    constructor(
        private val zippedFilesReader: ZippedFilesReader,
        private val tocParser: TocParser,
        private val chapterParser: ChapterParser,
    ) {
        suspend fun parse(bookFile: File) =
            withContext(Dispatchers.Default) {
                val files: Map<String, EpubFile> =
                    bookFile.inputStream().buffered().use { inputStream ->
                        zippedFilesReader.getZippedFiles(inputStream)
                    }

                val container: EpubFile =
                    files["META-INF/container.xml"]
                        ?: throw FileNotFoundException("Container file not found")

                val opfFilePath: String? =
                    Ksoup
                        .parseXml(container.data.decodeToString())
                        .selectFirst("rootfile")
                        ?.attr("full-path")
                        ?.decodedUrl
                val opfFile: EpubFile =
                    files[opfFilePath]
                        ?: throw FileNotFoundException("OPF file not found")
                val opfDoc: Document = Ksoup.parseXml(opfFile.data.decodeToString())

                val metadata: Element =
                    opfDoc.selectFirst("metadata")
                        ?: throw NoSuchElementException("Metadata element not found")
                val title =
                    metadata.selectFirst("dc|title")?.ownText()
                        ?: metadata.selectFirst("title")?.ownText()
                        ?: ""
                val author: String? =
                    metadata.selectFirst("dc|creator")?.ownText()
                        ?: metadata.selectFirst("creator")?.ownText()
                val description: String? =
                    metadata.selectFirst("description")?.ownText()
                        ?: metadata.selectFirst("dc|description")?.ownText()

                val hrefRootPath = File(opfFilePath).parentFile ?: File("")

                val manifest: Element =
                    opfDoc.selectFirst("manifest")
                        ?: throw NoSuchElementException("Manifest element not found")
                val manifestItems: Map<String, EpubBook.ManifestItem> =
                    manifest
                        .children()
                        .map {
                            EpubBook.ManifestItem(
                                id = it.attr("id"),
                                hrefFullPath = it.attr("href").findHrefFullPath(hrefRootPath),
                                mediaType = it.attr("media-type"),
                                properties = it.attr("properties"),
                            )
                        }.associateBy { it.id }

                val tocFilePath: String? =
                    manifestItems
                        .values
                        .firstOrNull {
                            it.properties.contains("nav") || it.properties.contains("toc")
                        }?.hrefFullPath
                val tocFile: EpubFile =
                    files[tocFilePath]
                        ?: throw FileNotFoundException("Toc file missing")
                val tocEntries: List<EpubBook.TocEntry> = tocParser.parse(tocFile, hrefRootPath)

                val spine: Element =
                    opfDoc.selectFirst("spine")
                        ?: throw NoSuchElementException("Spine element not found")
                val chapters: List<EpubBook.Chapter> =
                    chapterParser.parse(
                        hrefRootPath = hrefRootPath,
                        manifestItems = manifestItems,
                        tocEntries = tocEntries,
                        spine = spine,
                    )

                val coverPath: String? =
                    manifestItems
                        .values
                        .firstOrNull {
                            it.properties.contains("cover")
                        }?.hrefFullPath

                EpubBook(
                    title = title,
                    author = author,
                    description = description,
                    coverPath = coverPath,
                    chapters = chapters,
                    toc = tocEntries,
                )
            }
    }
