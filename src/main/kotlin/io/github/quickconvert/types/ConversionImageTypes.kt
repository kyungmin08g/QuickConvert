package io.github.quickconvert.types

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import org.springframework.core.io.FileSystemResource
import java.io.ByteArrayInputStream
import java.io.File

fun ffmpegProcessStart(command: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
    val processBuilder = ProcessBuilder(command.trim().split(" "))
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()

    val inputStream = ByteArrayInputStream(fileByteArray)
    inputStream.copyTo(process.outputStream)
    process.outputStream.close()

    val fileResource = FileSystemResource(File(conversionFileName))

    return if (process.waitFor() != 0) {
        println("ffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.")
        FileResponseObject("none")
    } else {
        println("ffmpeg 프로세스가 정상적으로 종료되었습니다.")
        FileResponseObject(fileResource.filename)
    }
}

enum class ConversionImageTypes(val fileType: String): FFmpegProcess {
    JPG("jpg") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    PNG("png") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    JPEG("jpeg") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    GIF("gif") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    BMP("bmp") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    TIFF("tiff") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
    WEBP("webp") {
        override fun ffmpegProcess(fileName: String, fileByteArray: ByteArray): FileResponseObject {
            val conversionFileName = "${fileName.substringBeforeLast(".").replace("+", "").replace(" ", "")}.${this.fileType}"
            val command = """
               ffmpeg -i pipe:0 $conversionFileName 
            """
            return ffmpegProcessStart(command, conversionFileName, fileByteArray)
        }
    },
//    PDF("pdf"),
//    DOCX("docx"),
//    TXT("txt"),
//    SVG("svg"),
//    ICO("ico"),
//    PSD("psd")
}