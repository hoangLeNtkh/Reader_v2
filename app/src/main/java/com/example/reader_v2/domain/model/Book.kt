package com.example.reader_v2.domain.model

data class Book(
    val id: String,
    val filePath: String,
    val title: String,
    val author: String?,
    val description: String?,
    val totalChapters: Int,
    val chapters: List<SimpleChapter>,
)
