package com.example.reader_v2.domain

import android.net.Uri
import com.example.reader_v2.data.repository.BookRepository
import com.example.reader_v2.domain.utils.Outcome
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AddAndExtractBookUseCase
    @Inject
    constructor(
        private val bookRepository: BookRepository,
    ) {
        suspend operator fun invoke(uri: Uri): Outcome<String, Exception> =
            try {
                val bookId = bookRepository.addAndExtractBook(uri)
                Outcome.Success(bookId)
            } catch (e: Exception) {
                Outcome.Error(e)
            }
    }
