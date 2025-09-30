package com.example.reader_v2.data.repository

import android.net.Uri
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.data_source.BookFileDataSource
import com.example.reader_v2.data.entity.BookEntity
import com.example.reader_v2.domain.model.Book
import com.example.reader_v2.domain.model.SimpleChapter
import com.example.reader_v2.epub_parser.EpubParser
import com.example.reader_v2.epub_parser.model.EpubBook
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

@Singleton
class BookRepositoryImpl
    @Inject
    constructor(
        private val fileDataSource: BookFileDataSource,
        private val epubParser: EpubParser,
        private val bookDao: BookDao,
    ) : BookRepository {
        override fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks().map { bookList -> bookList.map { it.toModel() } }

        override suspend fun getBookById(bookId: String): Book? = bookDao.getBookById(bookId)?.toModel()

        override suspend fun addAndExtractBook(uri: Uri): String =
            withContext(Dispatchers.IO) {
                val bookId = UUID.randomUUID().toString()
                val fileName = uri.lastPathSegment ?: "Unknown"

                val bookFile = fileDataSource.saveBookToAppStorage(uri, bookId)

                val epubBook: EpubBook = epubParser.parse(bookFile)

                val simpleChapters: List<SimpleChapter> =
                    epubBook.chapters.map { chapter ->
                        SimpleChapter(title = chapter.title, filePath = chapter.filePath)
                    }

                val bookEntity =
                    BookEntity(
                        id = bookId,
                        filePath = bookFile.absolutePath,
                        title = fileName,
                        author = epubBook.author,
                        description = epubBook.description,
                        totalChapters = epubBook.chapters.size,
                        chapters = simpleChapters,
                        coverPath = epubBook.coverPath,
                        lastReadChapterIndex = 0,
                        lastReadPosition = 0f,
                        dateAdded = System.currentTimeMillis(),
                        lastReadDate = System.currentTimeMillis(),
                        readProgress = 0f,
                    )

                bookDao.insertBook(bookEntity)

                fileDataSource.extractEpub(bookFile, bookId)

                fileName
            }

        override fun getChapterUrl(
            bookId: String,
            chapterFilePath: String,
        ): String = fileDataSource.getChapterFileUrl(bookId, chapterFilePath)

        private fun BookEntity.toModel(): Book =
            Book(
                id = id,
                filePath = filePath,
                title = title,
                author = author,
                description = description,
                totalChapters = totalChapters,
                chapters = chapters,
                coverPath = coverPath,
                lastReadChapterIndex = lastReadChapterIndex,
                lastReadPosition = lastReadPosition,
                dateAdded = dateAdded,
                lastReadDate = lastReadDate,
                readProgress = readProgress,
            )
    }
