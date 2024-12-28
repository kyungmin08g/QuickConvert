package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.VideoConversionService
import io.github.quickconvert.types.VideoTypes
import org.springframework.stereotype.Service

@Service
class VideoConversionServiceImpl : VideoConversionService() {
    override fun convertVideo(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            VideoTypes.Conversion.MP4.name -> VideoTypes.Conversion.MP4.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.MOV.name -> VideoTypes.Conversion.MOV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.MPEG.name -> VideoTypes.Conversion.MPEG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.WEBM.name -> VideoTypes.Conversion.WEBM.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.FLV.name -> VideoTypes.Conversion.FLV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.AVI.name -> VideoTypes.Conversion.AVI.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.GIF.name -> VideoTypes.Conversion.GIF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none", null)
        }
    }
}