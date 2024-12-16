package io.github.quickconvert.dto

data class FileInfo(
    val fileName: String,
    val conversionType: String,
    val fileByteArray: ByteArray
)
