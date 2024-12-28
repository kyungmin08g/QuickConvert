package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.DataFileConversionService
import io.github.quickconvert.types.DataFileTypes
import org.springframework.stereotype.Service

@Service
class DataFileConversionServiceImpl : DataFileConversionService() {
    override fun convertDataFile(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            DataFileTypes.Conversion.JSON.name -> DataFileTypes.Conversion.JSON.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            DataFileTypes.Conversion.XML.name -> DataFileTypes.Conversion.XML.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            DataFileTypes.Conversion.CSV.name -> DataFileTypes.Conversion.CSV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none", null)
        }
    }
}