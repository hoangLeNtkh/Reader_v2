package com.example.reader_v2.data.model

import com.example.reader_v2.data.entity.BookEntity
import org.readium.r2.shared.publication.Contributor
import org.readium.r2.shared.publication.Locator

data class Book(
	val id: String,
	val filePath: String,
	val title: String?,
	val contributors: List<Contributor>?,
	val description: String?,
	val coverImagePath: String?,
	val progression: Float,
	val lastReadLocation: String?,
	val addedDate: Long,
	val lastReadDate: Long
)