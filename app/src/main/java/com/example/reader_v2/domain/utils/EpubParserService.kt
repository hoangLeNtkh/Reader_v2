package com.example.reader_v2.domain.utils

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
        suspend fun parseBook(epubFile: File): EpubBook =
            withContext(Dispatchers.Default) {
                epubFile.inputStream().buffered().use { epubParser.parse(it) }
            }
    }
