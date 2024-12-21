package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.AudioConversionService
import io.github.quickconvert.types.AudioTypes
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

@Service
class AudioConversionServiceImpl : AudioConversionService() {
    override fun convertAudio(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            AudioTypes.Conversion.MP3.name -> AudioTypes.Conversion.MP3.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.WAV.name -> AudioTypes.Conversion.WAV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.FLAC.name -> AudioTypes.Conversion.FLAC.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            AudioTypes.Conversion.AAC.name -> AudioTypes.Conversion.AAC.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none", null)
        }
    }

    override fun conversionFileDownload(fileName: String, response: HttpServletResponse) {
        val decodedFile = File(URLDecoder.decode(fileName, "UTF-8"))
        val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
        val fileResource = FileSystemResource(fileName)

        response.apply {
            if (fileName.substringAfterLast(".") == "pdf") {
                this.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$encodedFileName\"")
                this.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                this.setContentLength(decodedFile.length().toInt())
            } else {
                this.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$encodedFileName\"")
                this.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                this.setContentLength(decodedFile.length().toInt())
            }
        }

        fileResource.inputStream.use { inputStream -> inputStream.copyTo(response.outputStream) }
        response.flushBuffer()

        decodedFile.delete()
    }
}