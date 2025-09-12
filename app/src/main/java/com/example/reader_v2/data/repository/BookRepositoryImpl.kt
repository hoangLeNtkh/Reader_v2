package com.example.reader_v2.data.repository

import android.net.Uri
import com.example.reader_v2.data.dao.BookDao
import com.example.reader_v2.data.entity.BookEntity
import com.example.reader_v2.data.helper.BookFileDataSource
import com.example.reader_v2.domain.helper.EpubParserService
import com.example.reader_v2.domain.model.Book
import com.example.reader_v2.domain.model.SimpleChapter
import com.example.reader_v2.epub_parser.model.EpubBook
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

@Singleton
class BookRepositoryImpl
    @Inject
    constructor(
        private val fileDataSource: BookFileDataSource,
        private val epubParserService: EpubParserService,
        private val bookDao: BookDao,
    ) : BookRepository {
        override fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks().map { bookList -> bookList.map { it.toModel() } }

        override suspend fun addAndExtractBook(uri: Uri): String {
            val bookId: String = UUID.randomUUID().toString()
            var bookFilePath: File? = null
            val fileName = uri.lastPathSegment ?: "Unknown"

            bookFilePath = fileDataSource.saveBookToAppStorage(uri, bookId)

            val epubBook: EpubBook = epubParserService.parseEpub(bookFilePath)
            val simpleChapters: List<SimpleChapter> =
                epubBook.chapters.map { chapter ->
                    SimpleChapter(title = chapter.title, filePath = chapter.filePath)
                }
            val bookEntity =
                BookEntity(
                    id = bookId,
                    filePath = bookFilePath.absolutePath,
                    title = fileName,
                    author = epubBook.author,
                    description = epubBook.description,
                    totalChapters = epubBook.chapters.size,
                    chapters = simpleChapters,
                )

            bookDao.insertBook(bookEntity)
            fileDataSource.extractEpub(bookFilePath, bookId)

            return fileName
        }

        private fun BookEntity.toModel(): Book =
            Book(
                id = id,
                filePath = filePath,
                title = title,
                author = author,
                description = description,
                totalChapters = totalChapters,
                chapters = chapters,
            )
    }
