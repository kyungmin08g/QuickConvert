package io.github.quickconvert.service

import jakarta.servlet.http.HttpServletResponse

abstract class ImageConversionService {
    abstract fun conversionFileDownload(fileName: String, response: HttpServletResponse)
}