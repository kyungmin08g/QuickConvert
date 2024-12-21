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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    fun imageConversion(
        @RequestParam("filename") fileName: String,
        response: HttpServletResponse
    ) = imageService.conversionFileDownload(fileName, response)
}