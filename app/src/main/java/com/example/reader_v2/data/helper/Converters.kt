package com.example.reader_v2.data.helper

import androidx.room.TypeConverter
import com.example.reader_v2.domain.model.SimpleChapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
	@TypeConverter
	fun convertChapterListToJson(chapters: List<SimpleChapter>): String {
		return Gson().toJson(chapters)
	}

	@TypeConverter
	fun convertJsonToChapterList(chaptersJson: String?): List<SimpleChapter>? {
		if (chaptersJson.isNullOrEmpty()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<SimpleChapter>>() {}.type
		return Gson().fromJson(chaptersJson, listType)
	}
}