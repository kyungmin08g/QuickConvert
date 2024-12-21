package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import jakarta.servlet.http.HttpServletResponse

abstract class DataFileConversionService {
    abstract fun convertDataFile(fileInfo: FileInfo): FileResponseObject
    abstract fun conversionFileDownload(fileName: String, response: HttpServletResponse)
}