package io.github.quickconvert.service

import io.github.quickconvert.dto.FileResponseObject

interface FFmpegProcess {
    fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject
}