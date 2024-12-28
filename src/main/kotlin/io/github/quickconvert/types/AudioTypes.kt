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

@Slf4j
@Component
object AudioTypes : FFmpegProcess {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var stompTemplate: SimpMessagingTemplate

    @Autowired
    fun init(template: SimpMessagingTemplate) { this.stompTemplate = template }

    enum class Conversion(val fileType: String) : io.github.quickconvert.service.Conversion {
        MP3("mp3") {
            /*
                MP3 : O
                WAV : O
                FLAC : O
                AIFF : O
                M4A : O
                AAC : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$fileName").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a libmp3lame -qscale:a 0 -b:a 320k files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        WAV("wav") {
            /*
                MP3 : O
                WAV : O
                FLAC : O
                AIFF : O
                M4A : O
                AAC : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$fileName").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a pcm_s24le files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        FLAC("flac") {
            /*
                MP3 : O
                WAV : O
                FLAC : O
                AIFF : O
                M4A : O
                AAC : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$fileName").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a flac -compression_level 12 files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        AIFF("aiff") {
            /*
                MP3 : O
                WAV : O
                FLAC : O
                AIFF : O
                M4A : O
                AAC : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$fileName").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a pcm_s16le files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        M4A("m4a") {
            /*
                MP3 : O
                WAV : O
                FLAC : O
                AIFF : O
                M4A : O
                AAC : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$fileName").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -err_detect ignore_err -i ${restorationFile.absolutePath} -c:a aac -b:a 192k files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        },
        AAC("aac") {
            /*
                MP3 : O
                WAV : O
                FLAC : O
                AIFF : O
                M4A : O
                AAC : O
             */
            override fun conversion(fileName: String, fileByteArray: ByteArray): FileResponseObject {
                val conversionFileName = "${fileName.substringBeforeLast(".").replace(" ", "")}.${this.fileType}"
                val filename = "${fileName.substringBeforeLast(".").replace(" ", "")}.${fileName.substringAfterLast(".")}"

                val restorationFile = File("files/$fileName").also { it.createNewFile(); it.writeBytes(fileByteArray) }
                val command = """
                   ffmpeg -i ${restorationFile.absolutePath} -c:a aac -b:a 320k files/convert-$conversionFileName
                """

                return ffmpegProcess(command, filename, conversionFileName, fileByteArray)
            }
        }
    }

    override fun ffmpegProcess(command: String, fileName: String, conversionFileName: String, fileByteArray: ByteArray): FileResponseObject {
        val process = ProcessBuilder(command.trim().split(" ")).apply { this.redirectErrorStream(true) }.start()
        process.outputStream.close()

        val processInputStreamBytes = process.inputStream.readBytes()
        val outputLogs = File.createTempFile("ffmpegLogs", ".txt").also {
            it.deleteOnExit()
            it.writeBytes(processInputStreamBytes)
        }

        ByteArrayInputStream(processInputStreamBytes).bufferedReader().useLines { input ->
            input.forEach {
                // 파일 변환 크기 구하기
                var lastConvertSize: String? = null
                BufferedReader(InputStreamReader(ByteArrayInputStream(outputLogs.readBytes()))).use { lines ->
                    lines.readLines().forEach { line ->
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

                    // 실시간 변환 중인 파일의 크기 구하기
                    if (it.contains("size=")) {
                        // 각각의 크기
                        val convertSizeKB = it.substringAfterLast("size=").replace(" ", "").substringBeforeLast("KiB").toFloat() / 1.024f
                        val convertSizeMB = convertSizeKB / 1024.0f
                        val convertSizeGB = convertSizeMB / 1024.0f

                        val content = if (convertSizeKB >= 1024) { // MB일 경우
                            if (convertSizeMB >= 1024) { // GB일 경우
                                "${convertSizeGB.toString().let {
                                    it.substring(0, it.lastIndexOf(".") + 3)
                                }}GB / $lastConvertSize"
                            } else {
                                "${convertSizeMB.toString().let {
                                    if (it.lastIndexOf(".").toString().length == 2) it.substring(0, it.lastIndexOf(".") + 2)
                                    else it.substring(0, it.lastIndexOf(".") + 3)
                                }}MB / $lastConvertSize"
                            }
                        } else { // MB와 GB가 아닐 경우
                            "${convertSizeKB.toString().let {
                                if ((it.lastIndexOf(".")).toString().length <= 2) it.substring(0, it.lastIndexOf(".") + 2)
                                else it.substring(0, it.lastIndexOf(".") + 3)
                            }}KB / $lastConvertSize"
                        }

                        stompTemplate.convertAndSend("/sub/1", content)
                        Thread.sleep(200)
                    }
                }
            }
        }

        Thread.sleep(1000)
        val conversionFile = File("files/convert-$conversionFileName")
        val fileBytes = conversionFile.readBytes()

        return if (process.waitFor() != 0) {
            File("files/$fileName").delete()
            conversionFile.delete()
            log.error("\u001B[31mffmpeg 프로세스를 실행하던 도중 문제가 발생했습니다.\u001B[0m")
            return FileResponseObject("none", null)
        } else {
            File("files/$fileName").delete()
            conversionFile.delete()
            log.info("\u001B[34mffmpeg 프로세스가 정상적으로 처리되어 {} 파일이 {} 파일로 변환되었습니다.\u001B[0m", fileName, conversionFileName)
            FileResponseObject(conversionFileName, fileBytes)
        }
    }
}