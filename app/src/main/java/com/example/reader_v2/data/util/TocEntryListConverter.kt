package com.example.reader_v2.data.util

import androidx.room.TypeConverter
import com.example.reader_v2.domain.epub_parser.epub_model.EpubBook
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TocEntryListConverter {
	private val gson = Gson()

	@TypeConverter
	fun convertTocEntryListToJson(chapters: List<EpubBook.TocEntry>): String = gson.toJson(chapters)

	@TypeConverter
	fun convertJsonToTocEntryList(chaptersJson: String?): List<EpubBook.TocEntry>? {
		if (chaptersJson.isNullOrEmpty()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<EpubBook.TocEntry>>() {}.type
		return gson.fromJson(chaptersJson, listType)
	}
}