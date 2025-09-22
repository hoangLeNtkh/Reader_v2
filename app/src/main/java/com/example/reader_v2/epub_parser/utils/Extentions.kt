package com.example.reader_v2.epub_parser.utils

import java.io.File
import java.net.URLDecoder
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun ZipInputStream.getEntries(): Sequence<ZipEntry> = generateSequence { nextEntry }

fun String.findHrefFullPath(hrefRootPath: File): String {
    val resolvedFile = File(hrefRootPath, this).canonicalFile
    return resolvedFile.path.removePrefix("/")
}

fun String.asFileName(): String = replace(Regex("[^a-zA-Z0-9.-]"), "_")

val String.decodedUrl: String
    get() = URLDecoder.decode(this, "UTF-8")
