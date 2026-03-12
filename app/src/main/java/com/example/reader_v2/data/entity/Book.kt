package com.example.reader_v2.data.entity

import org.readium.r2.shared.publication.Contributor

data class Book(
	val id: String,
	val filePath: String,
	val title: String?,
	val author: List<Contributor>?,
	val description: String?,
	val coverPath: String?,
	val dateAdded: Long,
	val lastReadDate: Long
)