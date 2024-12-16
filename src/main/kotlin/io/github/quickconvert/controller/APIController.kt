package io.github.quickconvert.controller

import io.github.quickconvert.dto.FileInfo
import io.github.quickconvert.service.impl.ImageConversionServiceImpl
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
@Slf4j
class APIController(
    private val service: ImageConversionServiceImpl
) {
    private val logger = LoggerFactory.getLogger(APIController::class.java)

    @PostMapping("/conversion")
    fun imageConversion(@RequestBody fileInfo: FileInfo): ResponseEntity<FileInfo> {
        // 파일 이름
        println(fileInfo.fileName.substringBeforeLast("."))

        // 파일 확장자
        println(fileInfo.fileName.substringAfterLast(".").uppercase())

        // 변환할 확장자
        println(fileInfo.conversionType.uppercase())

        return ResponseEntity.status(HttpStatus.OK).body(fileInfo)
    }

}