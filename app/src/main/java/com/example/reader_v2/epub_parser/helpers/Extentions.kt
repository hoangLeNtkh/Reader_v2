package com.example.reader_v2.domain.epub_parser.helpers

import java.net.URLDecoder
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun ZipInputStream.getEntries(): Sequence<ZipEntry> = generateSequence { nextEntry }

val String.decodedUrl: String
		get() = URLDecoder.decode(this, "UTF-8")