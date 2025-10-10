package com.example.reader_v2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val filePath: String,
    val title: String,
    val author: String?,
    val description: String?,
    val totalChapters: Int,

    val coverPath: String?,
    val chapters: List<EpubBook.Chapter>,
    val toc: List<EpubBook.TocEntry>,

    val lastReadChapterIndex: Int,
    val lastReadPosition: Float,
    val readProgress: Float,

    val dateAdded: Long,
    val lastReadDate: Long,
)
