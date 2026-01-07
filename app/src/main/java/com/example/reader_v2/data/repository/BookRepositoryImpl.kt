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

@Singleton
class BookRepositoryImpl
    @Inject
    constructor(
        @dagger.hilt.android.qualifiers.ApplicationContext
        private val context: android.content.Context,
        private val fileDataSource: BookFileDataSource,
        private val epubParser: EpubParser,
        private val bookDao: BookDao,
    ) : BookRepository {
        override fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks().map { bookList -> bookList.map { it.toModel() } }

        override suspend fun getBook(bookId: String): Book = bookDao.getBookById(bookId)!!.toModel()

        override suspend fun addAndExtractBook(uri: Uri): String =
            withContext(Dispatchers.IO) {
                val bookId = calculateChecksum(uri)

                val existingBook = bookDao.getBookById(bookId)
                if (existingBook != null) {
                    return@withContext existingBook.title
                }

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

        override suspend fun updateReadingProgress(
            bookId: String,
            chapterIndex: Int,
            position: Float,
        ) {
            withContext(Dispatchers.IO) {
                bookDao.updateReadingProgress(
                    bookId = bookId,
                    chapterIndex = chapterIndex,
                    position = position,
                )
            }
        }

        private fun calculateChecksum(uri: Uri): String {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            context.contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(8192)
                var bytesRead = input.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = input.read(buffer)
                }
            }
            return digest.digest().joinToString("") { "%02x".format(it) }
        }

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
