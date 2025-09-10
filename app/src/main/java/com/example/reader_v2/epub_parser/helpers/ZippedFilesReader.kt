package com.example.reader_v2.epub_parser.helpers

import com.example.reader_v2.epub_parser.model.EpubFile
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.zip.ZipInputStream

@Singleton
class ZippedFilesReader @Inject constructor() {
	suspend fun getZippedFiles(inputStream: InputStream): Map<String, EpubFile> = withContext(Dispatchers.IO) {
		inputStream.use { stream ->
			stream.buffered().let {
				ZipInputStream(it).use { zis ->
					zis
						.getEntries()
						.filterNot { entry -> entry.isDirectory }
						.map { entry -> EpubFile(entry.name, zis.readBytes()) }
						.associateBy { entry -> entry.entryPath }
				}
			}
		}
	}
}