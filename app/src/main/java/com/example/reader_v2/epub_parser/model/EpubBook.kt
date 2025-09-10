package com.example.reader_v2.epub_parser.model

data class EpubBook(
	val fileName: String,
	val title: String,
	val author: String?,
	val description: String?,
	val chapters: List<Chapter>
) {
	data class ManifestItem(
		val id: String,
		val hrefFullPath: String,
		val mediaType: String,
		val properties: String
	)

	data class TocEntry(
		val chapterTitle: String,
		val chapterLink: String,
		val children: List<TocEntry>
	)

	data class Chapter(
		val title: String,
		val fragmentLink: String,
		val filePath: String,
	)
}