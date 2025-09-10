package com.example.reader_v2.epub_parser

import com.example.reader_v2.epub_parser.helpers.ChapterParser
import com.example.reader_v2.epub_parser.helpers.TocParser
import com.example.reader_v2.epub_parser.helpers.ZippedFilesReader
import com.example.reader_v2.epub_parser.helpers.decodedUrl
import com.example.reader_v2.epub_parser.helpers.findHrefFullPath
import com.example.reader_v2.epub_parser.model.Chapter
import com.example.reader_v2.epub_parser.model.EpubFile
import com.example.reader_v2.epub_parser.model.ManifestItem
import com.example.reader_v2.epub_parser.model.TocEntry
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

@Singleton
class EpubParser @Inject constructor(
	private val zippedFilesReader: ZippedFilesReader,
	private val tocParser: TocParser,
	private val chapterParser: ChapterParser
) {
	suspend fun parse(inputStream: InputStream) {
		withContext(Dispatchers.Default) {
			val files: Map<String, EpubFile> = zippedFilesReader.getZippedFiles(inputStream)
			val container: EpubFile = files["META-INF/container.xml"]
				?: throw FileNotFoundException("Container file not found")

			val opfFilePath: String? = Ksoup.parseXml(container.data.decodeToString())
				.selectFirst("rootfile")?.attr("full-path")?.decodedUrl
			val opfFile: EpubFile = files[opfFilePath]
				?: throw FileNotFoundException("OPF file not found")
			val opfDoc: Document = Ksoup.parseXml(opfFile.data.decodeToString())

			val metadata: Element = opfDoc.selectFirst("metadata")
				?: throw NoSuchElementException("Metadata element not found")
			val title = metadata.selectFirst("dc|title")?.ownText()
				?: metadata.selectFirst("title")?.ownText()
				?: ""
			val author: String? = metadata.selectFirst("dc|creator")?.ownText()
				?: metadata.selectFirst("creator")?.ownText()
			val description: String? = metadata.selectFirst("description")?.ownText()
				?: metadata.selectFirst("dc|description")?.ownText()

			val hrefRootPath: File = File(opfFilePath).parentFile ?: File("")

			val manifest: Element = opfDoc.selectFirst("manifest")
				?: throw NoSuchElementException("Manifest element not found")
			val manifestItems: Map<String, ManifestItem> = manifest.children().map {
				ManifestItem(
					id = it.attr("id"),
					hrefFullPath = it.attr("href").findHrefFullPath(hrefRootPath),
					mediaType = it.attr("media-type"),
					properties = it.attr("properties")
				)
			}.associateBy { it.id }

			val tocFilePath: String? = manifestItems["toc"]?.hrefFullPath
				?: manifestItems["nav"]?.hrefFullPath
				?: manifestItems.values.firstOrNull {
					it.properties.contains("nav") || it.properties.contains("toc")
				}?.hrefFullPath
			val tocFile: EpubFile = files[tocFilePath] ?: throw FileNotFoundException("Toc file missing")
			val tocEntries: List<TocEntry> = tocParser.parse(tocFile, hrefRootPath)

			val spine: Element = opfDoc.selectFirst("spine")
				?: throw NoSuchElementException("Spine element not found")
			val chapters = chapterParser.parse(
				hrefRootPath = hrefRootPath,
				manifestItems = manifestItems,
				tocEntries = tocEntries,
				spine = spine,
			)
		}
	}
}