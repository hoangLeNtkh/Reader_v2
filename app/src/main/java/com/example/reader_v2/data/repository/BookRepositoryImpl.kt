package com.example.reader_v2.data.repository

import android.net.Uri
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.data_source.BookFileDataSource
import com.example.reader_v2.data.entity.BookEntity
import com.example.reader_v2.domain.epub_parser.EpubParser
import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook
import com.example.reader_v2.domain.model.Book
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

        override suspend fun getBook(bookId: String): Book = bookDao.getBookById(bookId).toModel()

        override suspend fun addAndExtractBook(uri: Uri): String =
            withContext(Dispatchers.IO) {
                val bookId = UUID.randomUUID().toString()
                val bookFile = fileDataSource.saveBookToAppStorage(uri, bookId)
                val epubBook: EpubBook = epubParser.parse(bookFile)

                fileDataSource.extractEpub(bookFile, bookId)

                val fullCoverPath =
                    epubBook.coverPath?.let { relativePath ->
                        fileDataSource.getChapterFileUrl(bookId, relativePath)
                    }

                val bookEntity =
                    BookEntity(
                        id = bookId,
                        filePath = bookFile.absolutePath,
                        title = epubBook.title,
                        author = epubBook.author,
                        description = epubBook.description,
                        totalChapters = epubBook.chapters.size,
                        coverPath = fullCoverPath,
                        chapters = epubBook.chapters,
                        toc = epubBook.toc,
                        lastReadChapterIndex = 0,
                        lastReadPosition = 0f,
                        readProgress = 0f,
                        dateAdded = System.currentTimeMillis(),
                        lastReadDate = System.currentTimeMillis(),
                    )

                bookDao.insertBook(bookEntity)
                epubBook.title
            }

        override fun getFileUrl(
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
                coverPath = coverPath,
                chapters = chapters,
                toc = toc,
                lastReadChapterIndex = lastReadChapterIndex,
                lastReadPosition = lastReadPosition,
                readProgress = readProgress,
                dateAdded = dateAdded,
                lastReadDate = lastReadDate,
            )
    }
