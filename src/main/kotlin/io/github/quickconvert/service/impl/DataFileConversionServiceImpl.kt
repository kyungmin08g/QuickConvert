package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.DataFileConversionService
import io.github.quickconvert.types.DataFileTypes
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

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