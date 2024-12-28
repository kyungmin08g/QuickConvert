package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject

abstract class AudioConversionService {
    abstract fun convertAudio(fileInfo: FileInfo): FileResponseObject
}