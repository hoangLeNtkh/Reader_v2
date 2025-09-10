package com.example.reader_v2.domain.epub_parser

import com.example.reader_v2.domain.epub_parser.helpers.ZippedFilesReader
import com.example.reader_v2.domain.epub_parser.helpers.decodedUrl
import com.example.reader_v2.domain.epub_parser.model.EpubFile
import com.fleeksoft.ksoup.Ksoup
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream

@Singleton
class EpubParser @Inject constructor(
	private val zippedFilesReader: ZippedFilesReader
) {
	suspend fun parse(inputStream: InputStream) = withContext(Dispatchers.IO) {
		val files: Map<String, EpubFile> = zippedFilesReader.getZippedFiles(inputStream)
		val container: EpubFile = files["META-INF/container.xml"]
			?: throw FileNotFoundException("Container file not found")

		val opfFilePath: String = Ksoup.parseXml(container.data.decodeToString())
			.selectFirst("rootfile")?.attr("full-path")?.decodedUrl
	}
}