package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.VideoConversionService
import io.github.quickconvert.types.VideoTypes
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

@Service
class VideoConversionServiceImpl : VideoConversionService() {
    override fun convertVideo(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            VideoTypes.Conversion.MP4.name -> VideoTypes.Conversion.MP4.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.MOV.name -> VideoTypes.Conversion.MOV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.MKV.name -> VideoTypes.Conversion.MKV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.WEBM.name -> VideoTypes.Conversion.WEBM.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.FLV.name -> VideoTypes.Conversion.FLV.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.AVI.name -> VideoTypes.Conversion.AVI.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            VideoTypes.Conversion.GIF.name -> VideoTypes.Conversion.GIF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none", null)
        }
    }

//    override fun conversionFileDownload(fileName: String, response: HttpServletResponse) {
//        val decodedFile = File(URLDecoder.decode(fileName, "UTF-8"))
//        val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
//        val fileResource = FileSystemResource(fileName)
//
//        response.apply {
//            if (fileName.substringAfterLast(".") == "pdf") {
//                this.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$encodedFileName\"")
//                this.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
//                this.setContentLength(decodedFile.length().toInt())
//            } else {
//                this.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$encodedFileName\"")
//                this.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
//                this.setContentLength(decodedFile.length().toInt())
//            }
//        }
//
//        fileResource.inputStream.use { inputStream -> inputStream.copyTo(response.outputStream) }
//        response.flushBuffer()
//
//        decodedFile.delete()
//    }
}