package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.AudioConversionService
import io.github.quickconvert.types.AudioTypes
import org.springframework.stereotype.Service

@Service
class AudioConversionServiceImpl : AudioConversionService() {
    override fun convertAudio(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            AudioTypes.Conversion.MP3.name -> AudioTypes.Conversion.MP3.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.WAV.name -> AudioTypes.Conversion.WAV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.FLAC.name -> AudioTypes.Conversion.FLAC.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.AIFF.name -> AudioTypes.Conversion.AIFF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.M4A.name -> AudioTypes.Conversion.M4A.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.AAC.name -> AudioTypes.Conversion.AAC.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none", null)
        }
    }
}