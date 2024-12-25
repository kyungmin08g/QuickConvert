package io.github.quickconvert.types

import io.github.quickconvert.dto.FileResponseObject
import io.github.quickconvert.service.FFmpegProcess
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.util.*

@Slf4j
@Component
object AudioTypes : FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var stompTemplate: SimpMessagingTemplate

    @Autowired
    fun init(template: SimpMessagingTemplate) { this.stompTemplate = template }

    enum class Conversion(val fileType: String) : io.github.quickconvert.service.Conversion {
        MP3("mp3") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -b:a 320k $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        WAV("wav") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:a pcm_s16le $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        FLAC("flac") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:a flac -compression_level 12 $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        },
        AAC("aac") {
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".")}.${this.fileType}"
                val command = """
                   ffmpeg -i pipe:0 -c:a aac -b:a 320k $conversionFileName
                """
                return ffmpegProcess(command, fileName, conversionFileName, fileByteArray)
            }
        }
    }

    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val process = ProcessBuilder(command.trim().split(" ")).apply { this.redirectErrorStream(true) }.start()
        ByteArrayInputStream(fileByteArray).apply { this.copyTo(process.outputStream) }
        process.outputStream.close()

        val processInputStreamBytes = process.inputStream.readBytes()
        val outputLogs = File.createTempFile("ffmpegLogs", ".txt").apply {
            this.deleteOnExit()
            this.writeBytes(processInputStreamBytes)
        }

        BufferedReader(InputStreamReader(ByteArrayInputStream(processInputStreamBytes))).use { input ->
            var lastConvertSize: String? = null
            BufferedReader(InputStreamReader(ByteArrayInputStream(outputLogs.readBytes()))).use { lines ->
                lines.readLines().forEach { line ->

                    // 비디오 파일일 경우
                    if (line.contains("Lsize=")) {
                        val lastConvertSizeKB = line.substringAfterLast("Lsize=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                        val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
                        val lastConvertSizeGB = lastConvertSizeMB / 1024.0f

                        lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
                            if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
                            else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
                        } else "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB" // KB일 경우
                    }

                    // 오디오 파일일 경우
                    if (line.contains("out#0")) {
                        val lastConvertSizeKB = line.substringAfterLast("audio:").replace(" ", "").substringBefore("KiB").toFloat() / 1.024f
                        val lastConvertSizeMB = lastConvertSizeKB / 1024.0f
                        val lastConvertSizeGB = lastConvertSizeMB / 1024.0f

                        lastConvertSize = if (lastConvertSizeKB >= 1024) { // MB일 경우
                            if (lastConvertSizeMB >= 1024) "${lastConvertSizeGB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}GB" // GB일 경우
                            else "${lastConvertSizeMB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}MB"
                        } else "${lastConvertSizeKB.toString().let { it.substring(0, it.lastIndexOf(".") + 3) }}KB" // KB일 경우
                    }
                }
            }

            input.readLines().forEach { line ->
                if (line.contains("size=")) {
                    val convertSizeKB = line.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                    val convertSizeMB = convertSizeKB / 1024.0f
                    val convertSizeGB = convertSizeMB / 1024.0f

                    val content = if (convertSizeKB >= 1024) { // MB일 경우
                        if (convertSizeMB >= 1024) { // GB일 경우
                            "${convertSizeGB.toString().let {
                                it.substring(0, it.lastIndexOf(".") + 3)
                            }}GB / $lastConvertSize"
                        } else {
                            "${convertSizeMB.toString().let {
                                it.substring(0, it.lastIndexOf(".") + 3)
                            }}MB / $lastConvertSize"
                        }
                    } else { // MB와 GB가 아닐 경우
                        "${convertSizeKB.toString().let {
                            if ((it.lastIndexOf(".") + 1).toString().length < 3) it.substring(0, it.lastIndexOf(".") + 2)
                            else it.substring(0, it.lastIndexOf(".") + 3)
                        }}KB / $lastConvertSize"
                    }

                    stompTemplate.convertAndSend("/sub/1", content)
                    Thread.sleep(200)
                }
            }
        }

        val conversionFile = File(conversionFileName)
        val fileBytes = conversionFile.readBytes()

        return if (process.waitFor() != 0) {
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[0m")
            conversionFile.delete()
            return FileResponseObject("none", null)
        } else {
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[0m", fileName, conversionFileName)
            conversionFile.delete()
            FileResponseObject(conversionFileName, Base64.getEncoder().encodeToString(fileBytes))
        }
    }
}