package com.example.reader_v2.data.dao

import androidx.room.Dao
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
    suspend fun getBook(bookId: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBook(bookId: String)

    @Query("UPDATE books SET lastReadLocation = :lastReadPositionLocator, lastReadDate = :lastReadDate WHERE id = :bookId")
    suspend fun updateBook(bookId: String, lastReadPositionLocator: String, lastReadDate: Long)

    @Query("SELECT lastReadLocation FROM books WHERE id = :bookId")
    suspend fun getLocatorJson(bookId: String): String?
}
