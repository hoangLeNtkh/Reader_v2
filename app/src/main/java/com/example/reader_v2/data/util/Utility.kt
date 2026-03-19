package com.example.reader_v2.data.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.security.MessageDigest

@Singleton
class Utility @Inject constructor(
	@ApplicationContext
	private val context: Context
) {
	fun calculateChecksum(uri: Uri): String {
		val digest = MessageDigest.getInstance("SHA-256")
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
}