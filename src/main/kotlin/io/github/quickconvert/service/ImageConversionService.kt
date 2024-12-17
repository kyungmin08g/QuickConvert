package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import jakarta.servlet.http.HttpServletResponse

abstract class ImageConversionService {
    abstract fun convertImage(fileInfo: FileInfo): FileResponseObject
    abstract fun conversionFileDownload(fileName: String, response: HttpServletResponse)
}