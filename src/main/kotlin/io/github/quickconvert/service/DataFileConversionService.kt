package io.github.quickconvert.service

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject

abstract class DataFileConversionService {
    abstract fun convertDataFile(fileInfo: FileInfo): FileResponseObject
}