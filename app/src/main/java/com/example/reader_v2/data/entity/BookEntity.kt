package com.example.reader_v2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reader_v2.domain.model.SimpleChapter

@Entity(tableName = "books")
data class BookEntity(
	@PrimaryKey
	val id: String,
	val filePath: String,
	val title: String,
	val author: String?,
	val description: String?,
	val totalChapters: Int,
	val chapters: List<SimpleChapter>
)