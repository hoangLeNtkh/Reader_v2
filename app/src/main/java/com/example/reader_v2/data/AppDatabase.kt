package com.example.reader_v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.entity.BookEntity
import com.example.reader_v2.data.util.AuthorListConverters

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
@TypeConverters(AuthorListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
