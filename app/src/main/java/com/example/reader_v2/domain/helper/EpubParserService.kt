package com.example.reader_v2.domain.helper

import com.example.reader_v2.epub_parser.EpubParser
import com.example.reader_v2.epub_parser.model.EpubBook
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Singleton
class EpubParserService
    @Inject
    constructor(
        private val epubParser: EpubParser,
    ) {
        suspend fun parseEpub(epubFilePath: File): EpubBook =
            withContext(Dispatchers.IO) {
                epubFilePath.inputStream().buffered().use { epubParser.parse(it) }
            }
    }
