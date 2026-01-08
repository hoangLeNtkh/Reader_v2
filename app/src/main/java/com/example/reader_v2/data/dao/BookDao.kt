package com.example.reader_v2.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reader_v2.data.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: BookEntity)

    @Delete
    fun deleteBook(book: BookEntity)

    @Query(
        "UPDATE books SET lastReadChapterIndex = :chapterIndex, lastReadPosition = :position, lastReadDate = :timestamp WHERE id = :bookId",
    )
    fun updateReadingProgress(
        bookId: String,
        chapterIndex: Int,
        position: Float,
        timestamp: Long = System.currentTimeMillis(), // Automatically updates the "Last Read" time
    )

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)
}
