package com.example.reader_v2

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.reader_v2.data.AppDatabase
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.data.repository.BookRepositoryImpl
import com.example.reader_v2.data.repository.ReaderRepository
import com.example.reader_v2.data.repository.ReaderRepositoryImpl
import com.example.reader_v2.data.util.Utility
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideBookDao(appDatabase: AppDatabase) = appDatabase.bookDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBookRepository(bookRepositoryImpl: BookRepositoryImpl): BookRepository

    @Binds
    @Singleton
    abstract fun bindReaderRepository(readerRepositoryImpl: ReaderRepositoryImpl): ReaderRepository
}


