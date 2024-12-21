package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.ImageConversionService
import io.github.quickconvert.types.ConversionImageTypes
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

@Service
class ImageConversionServiceImpl : ImageConversionService() {
    override fun convertImage(fileInfo: FileInfo): FileResponseObject {
        return when (fileInfo.conversionType) {
            ConversionImageTypes.JPG.name -> ConversionImageTypes.JPG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.PNG.name -> ConversionImageTypes.PNG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.JPEG.name -> ConversionImageTypes.JPEG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.GIF.name -> ConversionImageTypes.GIF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.BMP.name -> ConversionImageTypes.BMP.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.TIFF.name -> ConversionImageTypes.TIFF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.WEBP.name -> ConversionImageTypes.WEBP.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.PDF.name -> ConversionImageTypes.PDF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.DOCX.name -> ConversionImageTypes.DOCX.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.TXT.name -> ConversionImageTypes.TXT.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.SVG.name -> ConversionImageTypes.SVG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.ICO.name -> ConversionImageTypes.ICO.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.PSD.name -> ConversionImageTypes.PSD.conversion(fileInfo.fileName, fileInfo.fileByteArray)
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