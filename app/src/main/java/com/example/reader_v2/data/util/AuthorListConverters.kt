package com.example.reader_v2.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.readium.r2.shared.publication.Contributor

class AuthorListConverters {
	private val gson = Gson()

	@TypeConverter
	fun fromContributorList(contributors: List<Contributor>?): String? {
		if (contributors == null) return null
		return gson.toJson(contributors)
	}

	@TypeConverter
	fun toContributorList(value: String?): List<Contributor>? {
		if (value == null) return null
		val type = object : TypeToken<List<Contributor>>() {}.type
		return gson.fromJson(value, type)
	}
}