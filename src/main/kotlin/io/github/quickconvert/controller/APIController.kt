package io.github.quickconvert.controller

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.impl.ImageConversionServiceImpl
import io.github.quickconvert.service.impl.VideoConversionServiceImpl
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class APIController(
    private val imageService: ImageConversionServiceImpl,
    private val videoService: VideoConversionServiceImpl
) {
    @PostMapping("/conversion")
    fun conversion(@RequestBody fileInfo: FileInfo): ResponseEntity<FileResponseObject> {
        val fileResponseObjectDto: FileResponseObject = when (fileInfo.selectValue) {
            "이미지 파일" -> imageService.convertImage(fileInfo)
            "비디오 파일" -> videoService.convertVideo(fileInfo)
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