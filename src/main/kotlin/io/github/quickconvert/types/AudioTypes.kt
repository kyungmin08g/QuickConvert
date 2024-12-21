package io.github.quickconvert.types

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

@Slf4j
object AudioTypes : FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)

    enum class Conversion(val fileType: String) : io.github.quickconvert.service.Conversion {
        MP3("mp3") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        WAV("wav") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        FLAC("flac") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        AAC("aac") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        }
    }

    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val processBuilder = ProcessBuilder(command.trim().split(" "))
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        val inputStream = ByteArrayInputStream(fileByteArray)
        inputStream.copyTo(process.outputStream)
        process.outputStream.close()

        val conversionFile = File(conversionFileName)
        val fileBytes = conversionFile.readBytes()

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[37m")
            conversionFile.delete()
            return FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[37m", fileName, conversionFileName)
            conversionFile.delete()
            FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fileBytes))
        }
    }
}