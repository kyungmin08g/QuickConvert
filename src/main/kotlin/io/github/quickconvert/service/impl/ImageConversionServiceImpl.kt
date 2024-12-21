package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.ImageConversionService
import io.github.quickconvert.types.ImageTypes
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
            ImageTypes.Conversion.JPG.name -> ImageTypes.Conversion.JPG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.PNG.name -> ImageTypes.Conversion.PNG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.JPEG.name -> ImageTypes.Conversion.JPEG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.GIF.name -> ImageTypes.Conversion.GIF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.BMP.name -> ImageTypes.Conversion.BMP.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.TIFF.name -> ImageTypes.Conversion.TIFF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.WEBP.name -> ImageTypes.Conversion.WEBP.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.PDF.name -> ImageTypes.Conversion.PDF.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.DOCX.name -> ImageTypes.Conversion.DOCX.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.TXT.name -> ImageTypes.Conversion.TXT.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.SVG.name -> ImageTypes.Conversion.SVG.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.ICO.name -> ImageTypes.Conversion.ICO.conversion(fileInfo.fileName, fileInfo.fileByteArray)
            ImageTypes.Conversion.PSD.name -> ImageTypes.Conversion.PSD.conversion(fileInfo.fileName, fileInfo.fileByteArray)
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