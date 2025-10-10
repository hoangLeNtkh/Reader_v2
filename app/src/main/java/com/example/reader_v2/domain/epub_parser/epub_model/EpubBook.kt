package com.example.reader_v2.domain.epub_parser.epub_model

data class EpubBook(
    val title: String,
    val author: String?,
    val description: String?,
    val coverPath: String?,
    val chapters: List<Chapter>,
    val toc: List<TocEntry>,
) {
    data class ManifestItem(
        val id: String,
        val hrefFullPath: String,
        val mediaType: String,
        val properties: String,
    )

    data class TocEntry(
        val title: String,
        val link: String,
        val children: List<TocEntry>,
    )

    data class Chapter(
        val title: String,
        val filePath: String,
    )
}
