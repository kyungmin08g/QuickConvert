package io.github.quickconvert.service

import io.github.quickconvert.dto.FileResponseObject

interface FFmpegProcess {
    fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject
}