package com.example.reader_v2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.readium.r2.shared.publication.Contributor

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val filePath: String,
    val title: String?,
    val contributors: List<Contributor>?,
    val description: String?,
    val coverImagePath: String?,
    val progression: Double,
    val lastReadLocation: String?,
    val addedDate: Long,
    val lastReadDate: Long
)
