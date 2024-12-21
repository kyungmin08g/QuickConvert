package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import jakarta.servlet.http.HttpServletResponse

abstract class VideoConversionService {
    abstract fun convertVideo(fileInfo: FileInfo): FileResponseObject
//    abstract fun conversionFileDownload(fileName: String, response: HttpServletResponse)
}