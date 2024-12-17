package io.github.quickconvert.controller

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.impl.ImageConversionServiceImpl
import io.github.quickconvert.types.ConversionImageTypes
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class APIController(private val imageService: ImageConversionServiceImpl) {

    @PostMapping("/conversion")
    fun imageConversion(@RequestBody fileInfo: FileInfo): ResponseEntity<FileResponseObject> {
        val fileResponseObject = when (fileInfo.conversionType) {
            ConversionImageTypes.JPG.name -> ConversionImageTypes.JPG.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.PNG.name -> ConversionImageTypes.PNG.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.JPEG.name -> ConversionImageTypes.JPEG.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.GIF.name -> ConversionImageTypes.GIF.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.BMP.name -> ConversionImageTypes.BMP.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.TIFF.name -> ConversionImageTypes.TIFF.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            ConversionImageTypes.WEBP.name -> ConversionImageTypes.WEBP.ffmpegProcess(fileInfo.fileName, fileInfo.fileByteArray)
            else -> FileResponseObject("none")
        }

        return ResponseEntity.ok().body(fileResponseObject)
    }

    @GetMapping("/fileDownload")
    fun imageConversion(
        @RequestParam("filename") fileName: String,
        response: HttpServletResponse
    ) = imageService.conversionFileDownload(fileName, response)

}