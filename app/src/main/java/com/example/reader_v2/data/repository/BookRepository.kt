package com.example.reader_v2.data.repository

import android.net.Uri

interface BookRepository {
	suspend fun addAndExtractBook(uri: Uri): String
}