package com.example.reader_v2.domain.epub_parser.epub_model

data class EpubFile(
    val entryPath: String,
    val data: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EpubFile
        if (entryPath != other.entryPath) return false
        if (!data.contentEquals(other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = entryPath.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
