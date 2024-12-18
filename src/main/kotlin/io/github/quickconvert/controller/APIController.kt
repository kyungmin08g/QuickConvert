package io.github.quickconvert.controller

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.impl.ImageConversionServiceImpl
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class APIController(private val imageService: ImageConversionServiceImpl) {

    @PostMapping("/conversion")
    fun imageConversion(@RequestBody fileInfo: FileInfo): ResponseEntity<FileResponseObject> {
        return ResponseEntity.ok().body(imageService.convertImage(fileInfo))
    }

    @GetMapping("/fileDownload")
    fun imageConversion(
        @RequestParam("filename") fileName: String,
        response: HttpServletResponse
    ) = imageService.conversionFileDownload(fileName, response)

}