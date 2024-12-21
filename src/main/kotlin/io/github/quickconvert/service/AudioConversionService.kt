package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import jakarta.servlet.http.HttpServletResponse

abstract class AudioConversionService {
    abstract fun convertAudio(fileInfo: FileInfo): FileResponseObject
    abstract fun conversionFileDownload(fileName: String, response: HttpServletResponse)
}