package com.example.reader_v2.epub_parser.model

data class ManifestItem(
	val id: String,
	val hrefFullPath: String,
	val mediaType: String,
	val properties: String
)

data class Chapter(
	val title: String,
	val fragmentLink: String,
	val filePath: String,
)

data class TocEntry(
	val chapterTitle: String,
	val chapterLink: String,
	val children: List<TocEntry>
)