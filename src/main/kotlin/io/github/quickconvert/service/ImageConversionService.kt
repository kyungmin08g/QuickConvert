package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject

abstract class ImageConversionService {
    abstract fun convertImage(fileInfo: FileInfo): FileResponseObject
}