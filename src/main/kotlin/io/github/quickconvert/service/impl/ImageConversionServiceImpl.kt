package io.github.quickconvert.service.impl

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import io.github.quickconvert.service.ImageConversionService
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

@Service
@Slf4j
class ImageConversionServiceImpl : ImageConversionService() {
    private val logger = LoggerFactory.getLogger(ImageConversionServiceImpl::class.java)

    override fun conversionFileDownload(fileName: String, response: HttpServletResponse) {
        val decodedFile = File(URLDecoder.decode(fileName, "UTF-8"))
        val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
        val fileResource = FileSystemResource(fileName)

        response.apply {
            this.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$encodedFileName\"")
            this.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
            this.setContentLength(decodedFile.length().toInt())
        }

        fileResource.inputStream.use { inputStream -> inputStream.copyTo(response.outputStream) }
        response.flushBuffer()

        decodedFile.delete()
    }
}