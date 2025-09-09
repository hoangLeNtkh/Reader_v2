package com.example.reader_v2.domain.epub_parser.helpers

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun ZipInputStream.getEntries(): Sequence<ZipEntry> = generateSequence { nextEntry }