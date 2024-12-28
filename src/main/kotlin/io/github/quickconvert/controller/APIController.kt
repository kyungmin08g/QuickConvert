package io.github.quickconvert.controller

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.impl.AudioConversionServiceImpl
import io.github.quickconvert.service.impl.DataFileConversionServiceImpl
import io.github.quickconvert.service.impl.ImageConversionServiceImpl
import io.github.quickconvert.service.impl.VideoConversionServiceImpl
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

@RestController
@RequestMapping("/api/v1")
@Slf4j
class APIController(
    private val imageService: ImageConversionServiceImpl,
    private val videoService: VideoConversionServiceImpl,
    private val audioService: AudioConversionServiceImpl,
    private val dataFileService: DataFileConversionServiceImpl
) {
    private val log = LoggerFactory.getLogger(APIController::class.java)

    @PostMapping("/conversion")
    fun conversion(@RequestBody fileInfo: FileInfo): ResponseEntity<FileResponseObject> {
        val fileResponseObjectDto: FileResponseObject = when (fileInfo.selectValue) {
            "이미지 파일" -> imageService.convertImage(fileInfo)
            "비디오 파일" -> videoService.convertVideo(fileInfo)
            "오디오 파일" -> audioService.convertAudio(fileInfo)
            "데이터 파일" -> dataFileService.convertDataFile(fileInfo)
            else -> FileResponseObject("none", null)
        }

        return ResponseEntity.ok().body(fileResponseObjectDto)
    }

    @GetMapping("/fileDownload")
    fun convertFileDownload(@RequestParam("filename") fileName: String, response: HttpServletResponse) {
        val decodedFile = File(URLDecoder.decode("files/convert-$fileName", "UTF-8"))
        val encodedFileName = URLEncoder.encode("files/convert-$fileName", "UTF-8")
        val fileResource = FileSystemResource("files/convert-$fileName")

        response.apply {
            this.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$encodedFileName\"")
            this.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
            this.setContentLength(decodedFile.length().toInt())
        }

        fileResource.inputStream.use { it.copyTo(response.outputStream) }
        response.flushBuffer()
        File("files/convert-$fileName").delete()
    }
}